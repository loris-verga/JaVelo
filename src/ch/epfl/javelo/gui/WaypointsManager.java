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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * La classe WaypointsManager gère l'affichage et l'interaction avec les points de passage.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class WaypointsManager {
        //todo tu vois ça Loris??

        private static final String EXTERIOR_PATH_TEXT = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
        private static final String EXTERIOR_PATH_STYLE_CLASS = "pin_outside";

        private static final String INTERIOR_PATH_TEXT = "M0-23A1 1 0 000-29 1 1 0 000-23";
        private static final String INTERIOR_PATH_STYLE_CLASS = "pin_inside";

        private static final String GENERAL_WAYPOINT_PIN_STYLE_CLASS = "pin";
        private static final String FIRST_WAYPOINT_PIN_STYLE_CLASS = "first";
        private static final String MIDDLE_WAYPOINT_PIN_STYLE_CLASS = "middle";
        private static final String LAST_WAYPOINT_PIN_STYLE_CLASS = "last";

        private static final int MAX_CLOSEST_NODE_DISTANCE = 1000;
        private static final String ERROR_MESSAGE = "Aucune route à proximité";

        //creation d'une map privé qui permet de renvoyer l'index au quelle se trouve un point de passage,
        //dans la liste de point de passage en ayant seulement accès son marqueur associer.
        private Map<Group,Integer> mapWaypointPinToIndex = new HashMap<>();

        private final Graph graph;
        private ObjectProperty<MapViewParameters> mapViewParametersProperty;
        private ObservableList<Waypoint> waypointList;
        private Consumer<String> errorConsumer;
        private Pane pane;

    /**
     * Le constructeur de WaypointManager initialise tous les attributs et ajoute un auditeur aux paramètres du fond de la carte,
     * qui quand celle ci change, on redessine tous les marqueurs de point de passage ,
     * et un auditeur à la liste des points de passage pour quand elle change, on recrée tous les marqueurs.
     * @param graph le graph ou se trouve les points de passage.
     * @param mapViewParametersProperty les paramètres du fond de la carte.
     * @param waypointList la liste des points de passage initiale.
     * @param errorConsumer error à signaler en cas d'erreur.
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersProperty,
                            ObservableList<Waypoint> waypointList, Consumer<String> errorConsumer){
        this.graph = graph;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointList = waypointList;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();

        pane.setPickOnBounds(false);

        //Quand les paramètres du fond de la carte change,
        //on re-dessine tous les marqueurs de point de passage.
        mapViewParametersProperty.addListener(e->{
            for (Group waypointPin: mapWaypointPinToIndex.keySet()) {
                drawWaypointPin(waypointPin);
            }
        });

        //Quand la liste des points de passage change,
        //on re-créer tous les marqueurs de point de passage.
        waypointList.addListener((ListChangeListener)e -> createWaypointPins());

        createWaypointPins();
    }

    /**
     * La méthode pane retourne le panneau ou se trouve les marqueurs des points de passage.
     * @return le panneau ou se trouve les marqueurs des points de passage.
     */
    public Pane pane(){
        return pane;
    }

    /**
     * La méthode createWaypointPins privée, permet de créer pour chaque point de passage dans la liste de point de passage
     * son marqueur de point de passage associer, et initialise les interactions entre la souris et les marqueurs.
     */
    private void createWaypointPins() {

        pane.getChildren().clear();
        mapWaypointPinToIndex.clear();

        for (int i = 0; i < waypointList.size(); i++) {

            Group waypointPin = createWaypointPin(i);

            //Quand la souris est maintenu pressé sur un marqueur de point de passage,
            //on le déplace en changeant ses coordonnées à celle de la souris.
            waypointPin.setOnMouseDragged(e ->{
                waypointPin.setLayoutX(e.getSceneX());
                waypointPin.setLayoutY(e.getSceneY());
            });

            //Quand la souris est juste clicker sur un marqueur de point de passage,
            //si la souris ne s'est pas déplacer during le click, alors on enlève le marqueur de point de passage,
            //mais si la souris s'est déplacer during le click,
            //alors on regarde si on peut déplacer ce point de passage à cet endroit,
            //et si on peut, l'ancien point de passage est remplacé dans la liste des points de passage
            //par le nouveau point de passage qui se trouve aux coordonnées de la souris,
            //sinon on redessine le marqueur de point de passage à la position avant le click.
            waypointPin.setOnMouseReleased(e ->{
                int index = mapWaypointPinToIndex.get(waypointPin);

                if(e.isStillSincePress()) {waypointList.remove(index);}

                else{
                    Waypoint newWaypoint = createWaypoint(e.getSceneX(),e.getSceneY());

                    if(newWaypoint != null) {waypointList.set(index, newWaypoint);}

                    else{drawWaypointPin(waypointPin);}
                }
            });


        }
    }

    /**
     * La méthode createWaypointPin privée, permet de créer un marqueur de point de passage
     * et de le placer sur le panneau grâce à l'index donné en paramètre.
     * @param index l'index du point de passage dans la liste des points de passage.
     * @return retourne le marqueur du point de passage à l'index donné dans la liste des points de passage.
     */
    private Group createWaypointPin(int index){

        SVGPath exteriorPath = new SVGPath();
        exteriorPath.getStyleClass().add(EXTERIOR_PATH_STYLE_CLASS);
        exteriorPath.setContent(EXTERIOR_PATH_TEXT);

        SVGPath interiorPath = new SVGPath();
        interiorPath.getStyleClass().add(INTERIOR_PATH_STYLE_CLASS);
        interiorPath.setContent(INTERIOR_PATH_TEXT);

        Group waypointPin = new Group(exteriorPath, interiorPath);

        waypointPin.getStyleClass().add(GENERAL_WAYPOINT_PIN_STYLE_CLASS);

        if (0 < index && index < waypointList.size() -1 ) {
           waypointPin.getStyleClass().add(MIDDLE_WAYPOINT_PIN_STYLE_CLASS);
        } else if (index == 0) {
            waypointPin.getStyleClass().add(FIRST_WAYPOINT_PIN_STYLE_CLASS);
        } else {
            waypointPin.getStyleClass().add(LAST_WAYPOINT_PIN_STYLE_CLASS);
        }

        mapWaypointPinToIndex.put(waypointPin,index);

        drawWaypointPin(waypointPin);

        pane.getChildren().add(waypointPin);

        return waypointPin;
    }

    /**
     * La méthode drawWaypointPin privée, permet de dessiner sur le panneau le marqueur du point de passage donné.
     * @param waypointPin le marqueur du point de passage.
     */
    private void drawWaypointPin(Group waypointPin){

        int index = mapWaypointPinToIndex.get(waypointPin);

        PointWebMercator posOfWaypoint = PointWebMercator.ofPointCh(waypointList.get(index).position());
        double posX = mapViewParametersProperty.get().viewX(posOfWaypoint);
        double posY = mapViewParametersProperty.get().viewY(posOfWaypoint);

        waypointPin.setLayoutX(posX);
        waypointPin.setLayoutY(posY);
    }

    /**
     * La méthode addWaypoint ajouter un nouveau point de passage à une position donnée, dans la liste des points de passage.
     * @param x la coordonnée x du panneau centrer en haut à gauche, que l'on veut placer le point de passage.
     * @param y la coordonnée y du panneau centrer en haut à gauche, que l'on veut placer le point de passage.
     */
    public void addWaypoint(double x, double y){

        Waypoint waypoint = createWaypoint(x,y);

        if(waypoint != null){
            waypointList.add(waypoint);
        }
    }

    /**
     * La méthode addWaypoint privée, crée et retourne un point de passage à la position donnée
     * @param x la coordonnée x du panneau centrer en haut à gauche, que l'on veut placer le point de passage.
     * @param y la coordonnée y du panneau centrer en haut à gauche, que l'on veut placer le point de passage.
     * @return un point de passage, ou null s'il n'existe pas de noeud à proximité du point de passage.
     */
    private Waypoint createWaypoint(double x, double y){

        PointCh position = mapViewParametersProperty.get().pointAt(x,y).toPointCh();

        int nodeClosestToWaypoint =
                graph.nodeClosestTo(position, MAX_CLOSEST_NODE_DISTANCE);

        if(nodeClosestToWaypoint != -1){
            Waypoint waypoint = new Waypoint(position, nodeClosestToWaypoint);
            return waypoint;
        }
        else {
            errorConsumer.accept(ERROR_MESSAGE);
            return null;
        }
    }
}
