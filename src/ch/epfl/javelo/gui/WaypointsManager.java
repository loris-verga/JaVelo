package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * La classe WaypointsManager gère l'affichage et l'interaction avec les points de passage.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class WaypointsManager {

        private static final int MAX_CLOSEST_NODE_DISTANCE = 1000;
        private final Graph graph;
        private ObjectProperty<MapViewParameters> property;
        private ObservableList<Waypoint> waypointList;
        private Consumer<String> errorConsumer;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            ObservableList<Waypoint> waypointList, Consumer<String> errorConsumer){
        this.graph = graph;
        this.property = property;
        this.waypointList = waypointList;
        this.errorConsumer = errorConsumer;
    }

    public Pane pane(){
    Pane pane = new Pane();

    for(Waypoint wpt : waypointList){
        SVGPath exteriorPath = new SVGPath();
        exteriorPath.setContent("pin_outside");

        SVGPath interiorPath = new SVGPath();
        interiorPath.setContent("pin_inside");

        Group groupWpt = new Group(exteriorPath,interiorPath);

        groupWpt.setLayoutX(property.get().viewX(PointWebMercator.ofPointCh(wpt.position())));
        groupWpt.setLayoutY(property.get().viewY(PointWebMercator.ofPointCh(wpt.position())));

        pane.getChildren().add(groupWpt);
    }
    return pane;
    }

    public void addWayPoint(int x, int y){

        PointCh position = new PointCh(x,y);
        int nodeClosestToWaypoint =
                graph.nodeClosestTo(position, MAX_CLOSEST_NODE_DISTANCE);

        if(nodeClosestToWaypoint != -1){
            Waypoint waypoint = new Waypoint(position, nodeClosestToWaypoint);
            waypointList.add(waypoint);
        }
        else{
            errorConsumer.accept("Aucune route à proximité");}
    }
}
