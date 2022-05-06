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




    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Route getRoute() {
        return route.get();
    }

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    public DoubleProperty getHighlightedPositionProperty(){
        return highlightedPosition;
    }

    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }


    private void cacheMemoryAdd(Pair<Pair<Integer, Integer>, Route> pair){
        if (cacheMemory.size()<100){
            cacheMemory.put(pair.getKey(), pair.getValue());
        }
        Iterator<Map.Entry<Pair<Integer,Integer>, Route>> iterator = cacheMemory.entrySet().iterator();
        Pair<Integer, Integer> oldestElement = iterator.next().getKey();
        cacheMemory.remove(oldestElement);
        cacheMemory.put(pair.getKey(), pair.getValue());

    }

    private void recalculateItinerary(){
        if (waypoints.size() < 2){
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
            firstWaypointToLink = secondWayPointToLink;
        }

        MultiRoute newItinerary = new MultiRoute(listOfSinglesRoutes);
        route.set(newItinerary);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(newItinerary, MAX_STEP_LENGTH));
    }

    private void setNullItinerary(){
        route.set(null);
        elevationProfile.set(null);
    }
}
