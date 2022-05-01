package ch.epfl.javelo.gui;


import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;


/**
 * La classe BaseMapManager gère l'affichage et l'interaction avec le fond de carte.
 *
 * @author Loris Verga (345661)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private boolean redrawNeeded;
    private final Pane pane;
    private final Canvas canvas;
    private final WaypointsManager waypointsManager;

    //Ces attributs permettent de sauvegarder la dernière position de la souris.
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
        //On redessine les tuiles lorsqu'on redimensionne la fenêtre.
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });



        //Changement du niveau de zoom :
        SimpleLongProperty minimumScrollTime = new SimpleLongProperty();
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

            //Récupération de la position de la souris :
            double mousePosX = e.getX(), mousePosY = e.getY();
            //Position WebMercator du point sous la souris.
            PointWebMercator oldPointMouse = mapViewParameters.pointAt(mousePosX,mousePosY);
            //Position WebMercator du point haut-gauche.
            PointWebMercator pointTopLeft = mapViewParameters.pointAt(0,0);
            //MapViewParameters avec nouveau niveau de zoom et coin haut-gauche inchangé.
            MapViewParameters newMapViewParameters = new MapViewParameters(newZoom,
                    pointTopLeft.xAtZoomLevel(newZoom), pointTopLeft.yAtZoomLevel(newZoom));

            //Nouveau point WebMercator situé sous la souris.
            PointWebMercator newPointMouse = newMapViewParameters.pointAt(mousePosX,mousePosY);
            //Nouveau point WebMercator du coin haut-gauche :
            PointWebMercator topLeft = newMapViewParameters.pointAt(0,0);
            //Point haut-droite :
            PointWebMercator topRight = newMapViewParameters.pointAt(canvas.getWidth(), 0);
            //Obtention du rapport de proportionnalité :
            double distance = topRight.x()-topLeft.x();
            double prop = canvas.getWidth() / distance;

            //Obtention des vecteurs de décalage du coin haut-gauche pour que le point situé sous la souris reste
            //inchangé après un changement de zoom :
            double vectorX = (oldPointMouse.x()-newPointMouse.x())*prop;
            double vectorY = (oldPointMouse.y()-newPointMouse.y())*prop;

            //Changement du MapViewParameters :
            mapViewParametersProperty.set(newMapViewParameters.withMinXY(
                    newMapViewParameters.minX()+vectorX,
                    newMapViewParameters.minY()+vectorY));
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
        mapViewParametersProperty.addListener(e-> redrawOnNextPulse());

        //On crée un nouveau point de passage quand la click sur la carte,
        //et si la souris ne bouge pas durant le click
        pane.setOnMouseClicked(e ->{
            if(e.isStillSincePress()){
                waypointsManager.addWaypoint(e.getX(), e.getY());
            }
        });
    }





    /**
     * La méthode pane retourne le panneau JavaFX affichant le fond de la carte.
     * @return un panneau de type Pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * La méthode diffX permet de connaître la différence entre le coin de la tuile haut-gauche et le coin de
     * la fenêtre selon la coordonnée x.
     * @return un double.
     */
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
    /**
     * La méthode diffX permet de connaître la différence entre le coin de la tuile haut-gauche et le coin de
     * la fenêtre selon la coordonnée y.
     * @return un double.
     */
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

    /**
     * La méthode tilesInArea permet d'obtenir les tuiles à afficher.
     * @param zoomLevel le niveau de zoom.
     * @param height la hauteur de la fenêtre.
     * @param width la largeur de la fenêtre.
     * @return un tableau à deux dimensions contenant les tuiles.
     */
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


    /**
     * La méthode redrawIfNeeded permet de redessiner les tuiles.
     */
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

    /**
     * La méthode redrawOnNextPulse permet de gérer la fréquence de dessin.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }






}