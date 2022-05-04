package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

/**
 * La classe RouteManager gère l'affichage de l'itinéraire et (une partie de) l'interaction avec lui.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class RouteManager {

    private Polyline line;
    private Circle circle;

    private Pane pane;
    private RouteBean routeBean;
    private ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty;
    private Consumer<String> errorConsumer;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty, Consumer<String> errorConsumer){

        this.routeBean = routeBean;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.errorConsumer = errorConsumer;

        this.pane = new Pane();
        pane.setPickOnBounds(false);

        if(routeBean.getRoute() != null) {
            createLine();
            createCercle();
        }

        routeBean.routeProperty().addListener((o,p,n)->{
            if(routeBean.getRoute() != null) {
                createLine();
                createCercle();
            }
            else{
                line.setVisible(false);
                circle.setVisible(false);
            }
        });

        mapViewParametersProperty.addListener((o,p,n) ->{
            int previousZoomLevel = p.zoomLevel();
            int currentZoomLevel = n.zoomLevel();
            if(routeBean.getRoute() != null) {
                if (previousZoomLevel != currentZoomLevel) {
                    createLine();
                    createCercle();
                } else {
                    double displacementX = n.minX() - p.minX();
                    double displacementY = n.minY() - p.minY();

                    line.setLayoutX(line.getLayoutX() - displacementX);
                    line.setLayoutY(line.getLayoutY() - displacementY);

                    circle.setCenterX(circle.getCenterX() - displacementX);
                    circle.setCenterY(circle.getCenterY() - displacementY);
                }
            }
        } );
    }

    public Pane pane(){
        return pane;
    }

    private void createLine(){
        pane.getChildren().remove(line);

        line = new Polyline();
        line.setVisible(true);
        line.setId("route");

        for (PointCh point : routeBean.getRoute().points()) {

            PointWebMercator pointWM = PointWebMercator.ofPointCh(point);

            double x = mapViewParametersProperty.get().viewX(pointWM);
            double y = mapViewParametersProperty.get().viewY(pointWM);

            line.getPoints().addAll(x, y);
        }

        pane.getChildren().add(line);
    }

    private void createCercle() {
        pane.getChildren().remove(circle);

        circle = new Circle();
        circle.setVisible(true);
        circle.setId("highlight");

        double positionOnRouteOfCircle = routeBean.getHighlightedPosition();

        PointCh positionOfCircleCh = routeBean.getRoute().pointAt(positionOnRouteOfCircle);
        PointWebMercator positionOfCircleWM = PointWebMercator.ofPointCh(positionOfCircleCh);

        double x = mapViewParametersProperty.get().viewX(positionOfCircleWM);
        double y = mapViewParametersProperty.get().viewY(positionOfCircleWM);

        circle.setCenterX(x);
        circle.setCenterY(y);

        circle.setOnMouseClicked(e ->{

            Point2D position2D = pane.localToParent(e.getX(),e.getY());
            PointCh positionCH = mapViewParametersProperty.get().pointAt(position2D.getX() , position2D.getY()).toPointCh();

            int index = routeBean.getRoute().indexOfSegmentAt(routeBean.getHighlightedPosition());
            int nodeClosestToWaypoint = routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition());

            Waypoint newWaypoint = new Waypoint(positionCH, nodeClosestToWaypoint);

            for(Waypoint waypoint : routeBean.getWaypoints()) {
                if(nodeClosestToWaypoint == waypoint.nodeIdClosestTo()){
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                    newWaypoint = null;
                }
            }
            if(newWaypoint != null ) {
                routeBean.getWaypoints().add(index + 1, newWaypoint);
            }
        });

        pane.getChildren().add(circle);
    }
}
