package ch.epfl.javelo.gui;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.Map;


/**
 * La classe BaseMapManager gère l'affichage et l'interaction avec le fond de carte.
 *
 * @author Loris Verga (345661)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private boolean redrawNeeded;
    private Pane pane;
    private Canvas canvas;
    private final WaypointsManager waypointsManager;


    private double lastPositionMouseX;
    private double lastPositionMouseY;

    //dimensions d'une tuile :
    private final double TILE_LENGTH = 256.0;


    /**
     * La classe possède un constructeur public qui prend en argument :
     * @param tileManager le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     *                    le gestionnaire des points de passage
     *                    une propriété JavaFX contenant les paramètres de la carte affichée.
     */
    public BaseMapManager(TileManager tileManager,WaypointsManager waypointsManager,   ObjectProperty<MapViewParameters> mapViewParametersProperty){
        this.tileManager = tileManager;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointsManager = waypointsManager;
        this.redrawNeeded = true;

        //Création d'un canevas :
        canvas = new Canvas();
        //On place le canevas dans un panneau. L'avantage est que le panneau
        //est redimensionné automatiquement.
        pane = new Pane(canvas);
        //On lie la largeur et la hauteur du canevas à celle du panneau.
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });




        SimpleLongProperty minimumScrollTime = new SimpleLongProperty();
        //Changement du niveau de zoom :
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d){ return;}
            long currentTime = System.currentTimeMillis();
            if (currentTime < minimumScrollTime.get()){ return;}
            minimumScrollTime.set(currentTime + 250);
            double zoomDelta = Math.signum(e.getDeltaY());
            MapViewParameters mapViewParameters = mapViewParametersProperty.get();
            int zoomLevel = mapViewParameters.zoomLevel();
            int newZoom = (int)Math.round(zoomLevel + zoomDelta);
            if (newZoom > 19){newZoom = 19;}
            else if (newZoom<8){newZoom = 8;}

            System.out.println(newZoom);



            double x = e.getX();
            double y = e.getY();
            PointWebMercator oldPointMouse = mapViewParameters.pointAt(x,y);

            PointWebMercator point = mapViewParameters.pointAt(0,0);

            MapViewParameters mapViewParameters1 = new MapViewParameters(newZoom,
                    point.xAtZoomLevel(newZoom), point.yAtZoomLevel(newZoom));

            PointWebMercator newPointMouse = mapViewParameters1.pointAt(x,y);

            PointWebMercator topLeft = mapViewParameters1.pointAt(0,0);
            PointWebMercator topRight = mapViewParameters1.pointAt(canvas.getWidth(), 0);
            double distance = topRight.x()-topLeft.x();
            double prop = canvas.getWidth() / distance;

            double vectorX = -newPointMouse.x()+oldPointMouse.x();
            vectorX = vectorX * prop;
            System.out.println(vectorX);
            double vectorY = -newPointMouse.y()+oldPointMouse.y();
            vectorY = vectorY * prop;


            mapViewParametersProperty.set(mapViewParameters1.withMinXY(mapViewParameters1.minX()+vectorX, mapViewParameters1.minY()+vectorY));
        });


        //On sauvegarde la position de la souris lorsque l'on presse click.
        pane.setOnMousePressed(e -> {
            lastPositionMouseX = e.getX();
            lastPositionMouseY = e.getY();
        });

        //On déplace le coin haut-gauche lorsque la souris se déplace.
        pane.setOnMouseDragged(e-> {
            double vectorX = e.getX()- lastPositionMouseX;
            double vectorY = e.getY()- lastPositionMouseY;
            mapViewParametersProperty.set(new MapViewParameters(mapViewParametersProperty.get().zoomLevel(),
                    mapViewParametersProperty.get().minX() - vectorX,
                    mapViewParametersProperty.get().minY() - vectorY));
            lastPositionMouseX = e.getX();
            lastPositionMouseY = e.getY();
        });

        //On applique les changements lorsque les paramètres de la carte changent.
        mapViewParametersProperty.addListener(e-> System.out.println("changement à mapViewParameters"));
        mapViewParametersProperty.addListener(e-> redrawOnNextPulse());

    }





    /**
     * La méthode pane retourne le panneau JavaFX affichant le fond de la carte.
     * @return un panneau de type Pane
     */
    public Pane pane(){
        return pane;
    }


    private double diffX(){
        //Coordonnée haut gauche de la carte.
        double minX = mapViewParametersProperty.get().minX();
        //Index de la tuile en haut gauche :
        int indexX = (int) Math.floor(minX / TILE_LENGTH);
        //Coordonnée haute-gauche de cette tuile :
        double topLeftX = indexX * TILE_LENGTH;
        //Différence entre la position de la tuile et la position du coin haut-gauche de la fenêtre :
        return minX - topLeftX;
    }

    private double diffY(){
        //Coordonnée haut gauche de la carte.
        double minY = mapViewParametersProperty.get().minY();
        //Index de la tuile en haut gauche :
        int indexY = (int) Math.floor(minY / TILE_LENGTH);
        //Coordonnée haute-gauche de cette tuile :
        double topLeftY = indexY * TILE_LENGTH;
        //Différence entre la position de la tuile et la position du coin haut-gauche de la fenêtre :
        return minY -topLeftY;
    }

    private TileManager.TileId[][] tilesInArea(int zoomLevel, double height, double width) {

        //On calcule le point d'extrémité de la fenêtre visible :

        double minX = mapViewParametersProperty.get().minX();
        double minY = mapViewParametersProperty.get().minY();

        //Index de la tuile en haut gauche :
        int indexX = (int) Math.floor(minX / TILE_LENGTH);
        int indexY = (int) Math.floor(minY / TILE_LENGTH);

        //Calcul du nombre de tuiles à ajouter en plus de la première tuile haut-gauche :
        int numberOfTilesToAddRight = (int) Math.ceil(width / TILE_LENGTH);
        int numberOfTilesToAddDown = (int) Math.ceil(height / TILE_LENGTH);
        TileManager.TileId [] [] arrayOfTiles = new TileManager.TileId [numberOfTilesToAddDown+1][numberOfTilesToAddRight+1];

        for (int y = 0; y <= numberOfTilesToAddDown; ++y) {
            for (int x = 0; x <= numberOfTilesToAddRight; ++x) {
                arrayOfTiles[y][x] = new TileManager.TileId(zoomLevel, indexX + x, indexY + y);
            }
        }
        return arrayOfTiles;
    }



    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        //Récupération du contexte graphique du canevas.
        GraphicsContext graphicsContextCanvas = canvas.getGraphicsContext2D();

        //Obtention du gestionnaire de tuile

        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        //On récupère les dimensions de la fenêtre :
        double height = canvas.getHeight();
        double width = canvas.getWidth();

        //Récupération du tableau de tuiles à dessiner :
        TileManager.TileId[][] arrayOfTiles = tilesInArea(
                mapViewParameters.zoomLevel(), height, width);

        //Dessin des tuiles :
        if (arrayOfTiles.length != 0 && arrayOfTiles[0].length != 0) {
            for (int y = 0; y < arrayOfTiles.length; ++y) {
                for (int x = 0; x < arrayOfTiles[0].length; ++x) {
                    graphicsContextCanvas.drawImage(
                            tileManager.imageForTileAt(arrayOfTiles[y][x]), x*256-diffX(), y*256-diffY());
                }
            }
        }

        redrawOnNextPulse();

    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }





    
}
