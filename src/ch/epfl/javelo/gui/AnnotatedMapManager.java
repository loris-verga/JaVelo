package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * La classe AnnotatedMapManager gère l'affichage de la carte annotée,
 * c'est-à-dire le fond de carte au-dessus duquel sont superposé l'itinéraire
 * et les points de passages.
 */
public final class AnnotatedMapManager {


    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;

    private final StackPane stackPane;

    private ObjectProperty<Point2D> mousePositionP;

    private final DoubleProperty highlightedPosP;

    private final DoubleProperty temporary = new SimpleDoubleProperty(Double.NaN);



    private final int ZOOM_LEVEL_BEGINNING = 12;
    private final int TOP_LEFT_X_BEGINNING = 543200;
    private final int TOP_LEFT_Y_BEGINNING = 370650;


    /**
     * L'unique constructeur de la classe prend en argument :
     * @param graph le graph du réseau routier.
     * @param tileManager le gestionnaire de tuiles OpenStreetMap
     * @param routeBean le bean de l'itinéraire.
     * @param errorConsumer un consommateur d'erreurs, permettant
     *                      de signaler une erreur.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                               Consumer<String> errorConsumer){

        MapViewParameters mapViewParameters = new MapViewParameters(ZOOM_LEVEL_BEGINNING,
                TOP_LEFT_X_BEGINNING,
                TOP_LEFT_Y_BEGINNING);

        SimpleObjectProperty<MapViewParameters> mapViewParametersProperty =
                new SimpleObjectProperty<>(mapViewParameters);


        ObservableList<Waypoint> waypoints = routeBean.getWaypoints();

        waypointsManager = new WaypointsManager(graph, mapViewParametersProperty, waypoints, errorConsumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);

        routeManager = new RouteManager(routeBean, mapViewParametersProperty, errorConsumer);

        this.stackPane = new StackPane();
        stackPane.getStylesheets().add("map.css");
        Pane baseMapPane = baseMapManager.pane();
        Pane itineraryPane = routeManager.pane();
        Pane waypointsPane = waypointsManager.pane();
        stackPane.getChildren().add(baseMapPane);
        stackPane.getChildren().add(itineraryPane);
        stackPane.getChildren().add(waypointsPane);

        highlightedPosP = new SimpleDoubleProperty(Double.NaN);



        mousePositionP = new SimpleObjectProperty<>();


        stackPane.setOnMouseMoved(e-> {
            Point2D point = new Point2D(e.getX(), e.getY());
            mousePositionP.set(point);
            System.out.println(point.getX());
        });

        stackPane.setOnMouseExited(e-> highlightedPosP.set(Double.NaN));



        mousePositionP.addListener(e->{
            System.out.println("a");
            if (routeBean.getRoute() != null) {
                Point2D point2D= mousePositionP.get();
                int zoomLevel = mapViewParameters.zoomLevel();
                PointCh pointUnderMouse = PointWebMercator.of(zoomLevel,
                        point2D.getX(), point2D.getY()).toPointCh();
                RoutePoint pointClosestToOnRouteRP = routeBean.getRoute().pointClosestTo(
                        pointUnderMouse);
                PointCh pointClosestToOnRoute = pointClosestToOnRouteRP.point();
                PointWebMercator pointClosestToWM = PointWebMercator.ofPointCh(pointClosestToOnRoute);
                Point2D pointClosestTo2D = new Point2D(
                        mapViewParameters.viewX(pointClosestToWM),
                         mapViewParameters.viewY(pointClosestToWM));
                Double distance = Math2.norm(
                        pointClosestTo2D.getX()-point2D.getX(),
                        pointClosestTo2D.getY()-point2D.getY());

                if (distance <= 15){
                    highlightedPosP.set(pointClosestToOnRouteRP.position());
                }
                else{
                    highlightedPosP.set(Double.NaN);
                }
            }
 });












    }


    /**
     * La méthode pane retourne le panneau contenant la carte annotée.
     * @return
     */
    public Pane pane(){
        return stackPane;
    }

    /**
     * La méthode mousePositionOnRouteProperty retourne la propriété
     * contenant la position du pointeur de la souris le long de l'itinéraire.
     * @return une DoubleProperty.
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        //TODO (really to do lol)
        return temporary;
    }
}
