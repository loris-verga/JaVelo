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
 * @<author Juan Bautista Iaconucci (342153)
 */
public final class RouteManager {

    private static final String ITINERARY_LINE_ID = "route";
    private static final double ITINERARY_LINE_INITIAL_X_VALUE = 0.0;
    private static final double ITINERARY_LINE_INITIAL_Y_VALUE = 0.0;

    private static final int HIGHLIGHT_DISK_RADIUS = 5;
    private static final String HIGHLIGHT_DISK_ID = "highlight";

    //Todo this too
    //private static final String ERROR_CONSUMER_MESSAGE ="Un point de passage est déjà présent à cet endroit !";

    private final Polyline line;
    private final Circle disk;

    private final Pane pane;
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty;
    //todo here too
    //private final Consumer<String> errorConsumer;

    /**
     * Le constructeur de RouteManager permet d'initialiser ses attributs, et ajoute un auditeur à l'itinéraire du routeBean
     * qui quand celle ci change on crée à nouveaux la ligne et le disque,
     * et un autre aux paramètres de la carte, pour pouvoir déplacer la line et le disque en fonctions des paramètres qui ont changée,
     * et un dernier sur le disque, pour pouvoir ajouter des points de passages intermédiaires sur l'itinéraire.
     * @param routeBean le bean de l'itinéraire.
     * @param mapViewParametersProperty les paramètres de la carte affichée.
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty){

        this.routeBean = routeBean;
        this.mapViewParametersProperty = mapViewParametersProperty;
        //todo this too
        //this.errorConsumer = errorConsumer;

        this.pane = new Pane();
        pane.setPickOnBounds(false);

        this.line = new Polyline();
        line.setId(ITINERARY_LINE_ID);
        line.setVisible(false);

        this.disk = new Circle(HIGHLIGHT_DISK_RADIUS);
        disk.setId(HIGHLIGHT_DISK_ID);
        disk.setVisible(false);

        //Quand on clique sur le disque,
        //on regarde s'il y a déjà un point de passage qui contient le nœud situer à la position de la souris,
        //si c'est le cas l'erreur est consommé,
        //sinon on ajoute le point de passage dans la liste des points de passage.
        disk.setOnMouseClicked(e ->{

            Point2D position2D = pane.localToParent(e.getX(),e.getY());
            PointCh positionCH = mapViewParametersProperty.get().pointAt(position2D.getX() , position2D.getY()).toPointCh();

            //TODO verify this change is correct
            int index = routeBean.indexOfNonEmptySegmentAt(routeBean.getHighlightedPosition());
            //int index = routeBean.getRoute().indexOfSegmentAt(routeBean.getHighlightedPosition());
            int nodeClosestToWaypoint = routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition());

            Waypoint newWaypoint = new Waypoint(positionCH, nodeClosestToWaypoint);

            //Todo delete this if works
            //for(Waypoint waypoint : routeBean.getWaypoints()) {
            //    if(nodeClosestToWaypoint == waypoint.nodeIdClosestTo()){
            //        errorConsumer.accept(ERROR_CONSUMER_MESSAGE);
            //        newWaypoint = null;
            //    }
            //}
            //if(newWaypoint != null ) {
                routeBean.getWaypoints().add(index + 1, newWaypoint);
            //}
        });

        //Quand l'itinéraire change,
        //si la route est définie alors on crée la ligne et le disque,
        //sinon on les rend invisible.
        routeBean.routeProperty().addListener((o,p,n)->{
            if(routeBean.getRoute() != null) {
                drawItineraryLine();
                drawHighlightDisk();
            }
            else{
                line.setVisible(false);
                disk.setVisible(false);
            }
        });

        //Quand les paramètres de la carte change,
        //si le niveau de zoom a changé on crée à nouveau la ligne et le disque,
        //sinon on les repositionne aux bonnes coordonnées.
        mapViewParametersProperty.addListener((o,p,n) ->{

            int previousZoomLevel = p.zoomLevel();
            int currentZoomLevel = n.zoomLevel();
            if(routeBean.getRoute() != null) {
                if (previousZoomLevel != currentZoomLevel) {
                    drawItineraryLine();
                    drawHighlightDisk();
                } else {
                    double displacementX = n.minX() - p.minX();
                    double displacementY = n.minY() - p.minY();

                    line.setLayoutX( line.getLayoutX() - displacementX);
                    line.setLayoutY(line.getLayoutY() - displacementY);

                    disk.setCenterX(disk.getCenterX() - displacementX);
                    disk.setCenterY(disk.getCenterY() - displacementY);
                }
            }
        } );


         //On dessine le disque quand la position mise en évidence change.
        routeBean.getHighlightedPositionProperty().addListener(
                (p,o, n) -> {
                    if (routeBean.getRoute() != null) {
                        drawHighlightDisk();
                    }
                }
        );

        pane.getChildren().addAll(line,disk);

        if(routeBean.getRoute() != null) {
            drawItineraryLine();
            drawHighlightDisk();
        }
    }

    /**
     * La méthode pane retourne le panneau contenant la ligne représentant l'itinéraire et le disque de mise en évidence.
     * @return le panneau contenant la ligne représentant l'itinéraire et le disque de mise en évidence.
     */
    public Pane pane(){
        return pane;
    }

    /**
     * La méthode createLine privée, permet de créer et dessiner la ligne représentant l'itinéraire.
     */
    private void drawItineraryLine(){
        line.setVisible(true);
        line.getPoints().clear();

        line.setLayoutX(ITINERARY_LINE_INITIAL_X_VALUE);
        line.setLayoutY(ITINERARY_LINE_INITIAL_Y_VALUE);

        for (PointCh point : routeBean.getRoute().points()) {

            PointWebMercator pointWM = PointWebMercator.ofPointCh(point);

            double x = mapViewParametersProperty.get().viewX(pointWM);
            double y = mapViewParametersProperty.get().viewY(pointWM);

            line.getPoints().addAll(x,y);
        }
    }

    /**
     * La méthode drawHighlightDisk privée, permet de placer et dessiner le disque de mise en évidence.
     */
    private void drawHighlightDisk() {
        double positionOnRouteOfCircle = routeBean.getHighlightedPosition();

        if (!(Double.isNaN(positionOnRouteOfCircle)) && routeBean.getHighlightedPositionProperty() != null){
            disk.setVisible(true);

            PointCh positionOfCircleCh = routeBean.getRoute().pointAt(positionOnRouteOfCircle);
            PointWebMercator positionOfCircleWM = PointWebMercator.ofPointCh(positionOfCircleCh);

            double x = mapViewParametersProperty.get().viewX(positionOfCircleWM);
            double y = mapViewParametersProperty.get().viewY(positionOfCircleWM);

            disk.setCenterX(x);
            disk.setCenterY(y);
        }
        else{
            disk.setVisible(false);
        }
    }
}
