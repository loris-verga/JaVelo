package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
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

    //Cette LinkedHashMap représente le cache mémoire qui contient les Routes déjà calculées.
    private final LinkedHashMap <Pair<Integer, Integer>, Route> cacheMemory;

    //Constante utile à ElevationProfileComputer
    private final double MAX_STEP_LENGTH = 5;


    /**
     * L'unique constructeur prend en argument un RouteComputer afin de déterminer le meilleur itinéraire reliant
     * deux points de passage.
     * @param routeComputer un calculateur d'itinéraire.
     */
    public RouteBean(RouteComputer routeComputer){

        //Constructions des propriétés.
        waypoints = new SimpleListProperty<>();
        route = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        elevationProfile = new SimpleObjectProperty<>();

        //Construction du cache mémoire
        cacheMemory = new LinkedHashMap<>();

        //Initialisation des attributs
        this.routeComputer = routeComputer;
        this.highlightedPosition.set(Double.NaN);

        //On fait en sorte que l'itinéraire soit recalculé lorsque les waypoints changent.
        waypoints.addListener((ListChangeListener) e -> recalculateItinerary());
        recalculateItinerary();
    }


    /**
     * La méthode getWayPoints retourne la liste des Waypoints.
     * @return une liste observable de Waypoints.
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * La méthode getRoute renvoie la route se situant dans la propriété.
     * @return une multiRoute.
     */
    public Route getRoute() {
        return route.get();
    }

    /**
     * La méthode routeProperty méthode renvoie la propriété contenant la Route.
     * @return la propriété JavaFX.
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }
    /**
     * La méthode getHighlightedPositionProperty renvoie la propriété contenant la position mise en évidence.
     * @return la propriété JavaFX.
     */
    public DoubleProperty getHighlightedPositionProperty(){
        return highlightedPosition;
    }
    /**
     * La méthode getHighlightedPosition renvoie la position mise en évidence.
     * @return un double.
     */
    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * La méthode setHighlightedPosition permet de modifier la position mise en évidence.
     * @param highlightedPosition un double.
     */
    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    /**
     * La méthode getElevationProfile retourne le profil contenu dans la propriété.
     * @return l'ElevationProfile.
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }

    /**
     * La méthode elevationProfileProperty renvoie la propriété contenant le profil.
     * @return une propriété JavaFX.
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * La méthode cacheMemoryAdd permet d'ajouter un itinéraire au cache.
     * @param pair une paire dont la clé sont les nœuds de départ et d'arrivée et la valeur
     *             est la SingleRoute associée.
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
     * La méthode recalculateItinerary permet de recalculer l'itinéraire complet.
     */
    private void recalculateItinerary(){
        if (waypoints.size()<2){
            setNullItinerary();
            return;
        }

        List<Route> listOfSinglesRoutes = new ArrayList<>();

        int firstWaypointToLink = waypoints.get(0).nodeIdClosestTo();
        Iterator<Waypoint> iterator = waypoints.listIterator(1);
        while (iterator.hasNext()){
            int secondWayPointToLink = iterator.next().nodeIdClosestTo();
            Pair<Integer, Integer> itineraryPair = new Pair<>(firstWaypointToLink, secondWayPointToLink);

            if (cacheMemory.containsKey(itineraryPair)){
                listOfSinglesRoutes.add(cacheMemory.get(itineraryPair));
            }
            else{
                Route newRoute = routeComputer.bestRouteBetween(firstWaypointToLink, secondWayPointToLink);
                if (newRoute == null){
                    setNullItinerary();
                    return;
                }
                listOfSinglesRoutes.add(newRoute);
                cacheMemoryAdd(new Pair<Pair<Integer, Integer>, Route>(itineraryPair, newRoute));
            }
        }

        MultiRoute newItinerary = new MultiRoute(listOfSinglesRoutes);
        route.set(newItinerary);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(newItinerary, MAX_STEP_LENGTH));
    }

    /**
     * La méthode setNullItinerary permet d'obtenir un itinéraire nul.
     */
    private void setNullItinerary(){
        route.set(null);
        elevationProfile.set(null);
    }
}
