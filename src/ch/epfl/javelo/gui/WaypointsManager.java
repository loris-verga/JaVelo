package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
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
        private static final int INDEX_OF_WAYPOINT_INDEX_IN_STYLE_CLASS_LIST = 2;
        private final Graph graph;
        private ObjectProperty<MapViewParameters> mapViewParametersProperty;
        private ObservableList<Waypoint> waypointList;
        private Consumer<String> errorConsumer;
        private Pane pane;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersProperty,
                            ObservableList<Waypoint> waypointList, Consumer<String> errorConsumer){
        this.graph = graph;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointList = waypointList;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        pane.setPickOnBounds(false);

        mapViewParametersProperty.addListener(e->{
            updateWayPoints();
        });

        this.waypointList.addListener((ListChangeListener)e -> updateWayPoints());

        updateWayPoints();
    }

    public Pane pane(){
        return pane;
    }

    public void updateWayPoints() {
        System.out.println("update");
        pane.getChildren().clear();

        for (int i = 0; i < waypointList.size(); i++) {

            SVGPath exteriorPath = new SVGPath();
            exteriorPath.getStyleClass().add("pin_outside");
            exteriorPath.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");

            SVGPath interiorPath = new SVGPath();
            interiorPath.getStyleClass().add("pin_inside");
            interiorPath.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

            Group groupWpt = new Group(exteriorPath, interiorPath);

            if (i != 0 && i != waypointList.size() - 1) {
                groupWpt.getStyleClass().addAll("pin", "middle");
            } else if (i == 0) {
                groupWpt.getStyleClass().addAll("pin", "first");
            } else {
                groupWpt.getStyleClass().addAll("pin", "last");
            }

            groupWpt.setLayoutX(mapViewParametersProperty.get().viewX(PointWebMercator.ofPointCh(waypointList.get(i).position())));
            groupWpt.setLayoutY(mapViewParametersProperty.get().viewY(PointWebMercator.ofPointCh(waypointList.get(i).position())));

            groupWpt.getStyleClass().add(String.valueOf(i));

            groupWpt.setOnMouseReleased(e ->{

                int index = Integer.valueOf(groupWpt.getStyleClass().get(2));

                if(e.isStillSincePress()) {
                    waypointList.remove(index);
                }
                else{
                    waypointList.set(index,createWaypoint(e.getSceneX(),e.getSceneY()));
                }
                });

            groupWpt.setOnMouseDragged(e ->{
                groupWpt.setLayoutX(e.getSceneX());
                groupWpt.setLayoutY(e.getSceneY());
            });

            pane.getChildren().add(groupWpt);
        }
    }

    private Waypoint createWaypoint(double x, double y){

        PointCh position = mapViewParametersProperty.get().pointAt(x,y).toPointCh();

        int nodeClosestToWaypoint =
                graph.nodeClosestTo(position, MAX_CLOSEST_NODE_DISTANCE);

        if(nodeClosestToWaypoint != -1){
            Waypoint wayPoint = new Waypoint(position, nodeClosestToWaypoint);
            return wayPoint;
        }
        else{
            errorConsumer.accept("Aucune route à proximité");}
            return null;
    }

    public void addWayPoint(double x, double y){
        Waypoint waypoint = createWaypoint(x,y);

        if(waypoint != null){
            waypointList.add(waypoint);
        }
    }

}
