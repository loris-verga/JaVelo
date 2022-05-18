package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * La classe RouteBean est un bean JavaFX regroupant les propriétés relatives aux points de passage et à l'itinéraire
 * correspondant.
 *
 * @author Loris Verga (345661)
 */
public final class RouteBean {
    //La liste observable des points de passage :
    private final ObservableList<Waypoint> waypoints;
    //L'itinéraire permettant de relier les points de passages (en lecture seule) :
    private final ObjectProperty<Route> route;
    //La position le long de l'itinéraire, mise en évidence :
    private final DoubleProperty highlightedPosition;
    //Le profil de l'itinéraire (en lecture seule) :
    private final ObjectProperty<ElevationProfile> elevationProfile;
    //Le calculateur d'itinéraire
    private final RouteComputer routeComputer;

    //Cette LinkedHashMap représente le cache mémoire qui contient les 
    private final LinkedHashMap <Pair<Integer, Integer>, Route> cacheMemory;

    private final double MAX_STEP_LENGTH = 5;




    /**
     * L'unique constructeur prend en argument un RouteComputer afin de déterminer le meilleur itinéraire reliant
     * deux points de passage.
     * @param routeComputer un calculateur d'itinéraire.
     */
    public RouteBean(RouteComputer routeComputer){

        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        elevationProfile = new SimpleObjectProperty<>();

        cacheMemory = new LinkedHashMap<>();
        this.routeComputer = routeComputer;

        this.highlightedPosition.set(Double.NaN);

        waypoints.addListener((ListChangeListener) e -> recalculateItinerary());

        recalculateItinerary();

    }


    /**
     * La méthode getWaypoints
     * @return retourne une liste observable de waypoints.
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * La méthode getRoute
     * @return retourne la route que contient la propriété.
     */
    public Route getRoute() {
        return route.get();
    }

    /**
     * La méthode routeProperty
     * @return retourne la propriété qui contient la route.
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * La méthode getHighlightedPositionProperty
     * @return retourne la propriété qui contient la position mise en évidence.
     */
    public DoubleProperty getHighlightedPositionProperty(){
        return highlightedPosition;
    }

    /**
     * La méthode getHighlightedPosition retourne la position mise en évidence.
     * @return la position.
     */
    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * La méthode setHighlightedPosition permet de modifier la position mise en évidence.
     * @param highlightedPosition la nouvelle position mise en évidence le long de l'itinéraire.
     */
    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    /**
     * La méthode getElevationProfile
     * @return retourne le profil contenu dans la propriété.
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }

    /**
     * La méthode elevationProfileProperty
     * @return retourne la propriété qui contient le profil.
     */
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * La méthode cacheMemoryAdd permet d'ajouter une route au cache mémoire.
     * @param pair une paire constituée d'une part des nœuds de départ et d'arrivée et de
     *             l'autre part de la route.
     */
    private void cacheMemoryAdd(Pair<Pair<Integer, Integer>, Route> pair){
        if (cacheMemory.size()<100){
            cacheMemory.put(pair.getKey(), pair.getValue());
        }
        Iterator<Map.Entry<Pair<Integer,Integer>, Route>> iterator = cacheMemory.entrySet().iterator();
        Pair<Integer, Integer> oldestElement = iterator.next().getKey();
        cacheMemory.remove(oldestElement);
        cacheMemory.put(pair.getKey(), pair.getValue());

    }

    /**
     * La méthode recalculateItinerary permet de recalculer l'itinéraire.
     */
    private void recalculateItinerary(){
        if (waypoints.size() < 2){
            setNullItinerary();
            return;
        }

        List<Route> listOfSinglesRoutes = new ArrayList<>();

        int nodeIdOfFirstWaypointToLink = waypoints.get(0).nodeIdClosestTo();
        Iterator<Waypoint> iterator = waypoints.listIterator(1);

        while (iterator.hasNext()){
            int nodeIdOfSecondWaypointToLink = iterator.next().nodeIdClosestTo();

            if (nodeIdOfFirstWaypointToLink != nodeIdOfSecondWaypointToLink){
                Pair<Integer, Integer> itineraryPair = new Pair<>(nodeIdOfFirstWaypointToLink, nodeIdOfSecondWaypointToLink);

                if (cacheMemory.containsKey(itineraryPair)){
                    listOfSinglesRoutes.add(cacheMemory.get(itineraryPair));
                }
                else{
                    Route newRoute = routeComputer.bestRouteBetween(nodeIdOfFirstWaypointToLink, nodeIdOfSecondWaypointToLink);
                    if (newRoute == null){
                        setNullItinerary();
                        return;

                    }
                    listOfSinglesRoutes.add(newRoute);
                    cacheMemoryAdd(new Pair<>(itineraryPair, newRoute));
                }
            }
            nodeIdOfFirstWaypointToLink = nodeIdOfSecondWaypointToLink;
        }

        //On gère le cas où il y a uniquement deux waypoints appartenant au même nœud.
        if (listOfSinglesRoutes.isEmpty()){
            setNullItinerary();
            return;
        }

        MultiRoute newItinerary = new MultiRoute(listOfSinglesRoutes);
        route.set(newItinerary);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(newItinerary, MAX_STEP_LENGTH));
    }

    /**
     * La méthode setNullItinerary permet de rendre l'itinéraire courant nul.
     */
    private void setNullItinerary(){
        route.set(null);
        elevationProfile.set(null);
    }

    /**
     * La méthode indexOfNonEmptySegmentAt privée, variante d'indexOfSegmentAt de Route,
     * permet de retourner l'index du segment contenant la position donner.
     * @param position la position que l'on cherche l'index.
     * @return l'index du segment ou se trouve la position.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeIdClosestTo();
            int n2 = waypoints.get(i + 1).nodeIdClosestTo();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}
