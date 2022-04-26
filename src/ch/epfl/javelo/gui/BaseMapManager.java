package ch.epfl.javelo.gui;


import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
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
    private final WaypointsManager waypointsManager;


    //dimensions d'une tuile :
    private final double TILE_LENGTH = 256.0;


    /**
     * La classe possède un constructeur public qui prend en argument :
     * @param tileManager le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     *                    le gestionnaire des points de passage
     *                    une propriété JavaFX contenant les paramètres de la carte affichée.
     */
    //TODO ajouter des paramètres
    public BaseMapManager(TileManager tileManager,WaypointsManager wayPointsManager,  ObjectProperty<MapViewParameters> mapViewParametersProperty){
        this.tileManager = tileManager;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointsManager = wayPointsManager;

    }


    /**
     * La méthode pane retourne le panneau JavaFX affichant le fond de la carte.
     * @return
     */
    public Pane pane(){

        //Création d'un canevas :
        Canvas canvas = new Canvas();

        //On place le canevas dans un panneau. L'avantage est que le panneau
        //est redimensionné automatiquement.
        Pane pane = new Pane(canvas);

        //On lie la largeur et la hauteur du canevas à celle du panneau.
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        //Récupération du contexte graphique du canevas.
        GraphicsContext graphicsContextCanvas = canvas.getGraphicsContext2D();

        //Obtention du gestionnaire de tuile

        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        //On récupère les dimensions de la fenêtre :
        double height = canvas.getHeight();
        double width = canvas.getWidth();

        //Récupération du tableau de tuiles à dessiner :
        TileManager.TileId[][] arrayOfTiles = tilesInArea(mapViewParameters.topLeft(),
                mapViewParameters.zoomLevel(), height, width);

        //Dessin des tuiles :
        if (arrayOfTiles.length != 0 && arrayOfTiles[0].length != 0) {
            for (int y = 0; y < arrayOfTiles.length; ++y) {
                for (int x = 0; x < arrayOfTiles[0].length; ++x) {
                    graphicsContextCanvas.drawImage(
                            tileManager.imageForTileAt(arrayOfTiles[x][y]), x*256, y*256);
                }
            }
        }

        return pane;
    }



    private TileManager.TileId[][] tilesInArea(Point2D topLeft, int zoomLevel, double height, double width) {

        //On calcule les points d'extrémité de la fenêtre visible :
        double minX = topLeft.getX();
        double minY = topLeft.getY();

        //Index de la tuile en haut gauche :
        int indexX = (int) Math.floor(minX / TILE_LENGTH);
        int indexY = (int) Math.floor(minY / TILE_LENGTH);

        //Coins haut-gauches de cette tuile :
        double topLeftX = indexX * TILE_LENGTH;
        double topLeftY = indexY * TILE_LENGTH;

        //Différence entre la position de la tuile et la position du coin haut-gauche de la fenêtre :
        double diffX = minX - topLeftX;
        double diffY = minY - topLeftY;

        //Calcul du nombre de tuiles à ajouter en plus de la première tuile haut-gauche :
        int numberOfTilesToAddRight = (int) Math.ceil((width - diffX) / TILE_LENGTH);
        int numberOfTilesToAddDown = (int) Math.ceil((height - diffY) / TILE_LENGTH);

        TileManager.TileId [] [] arrayOfTiles = new TileManager.TileId [numberOfTilesToAddDown+1][numberOfTilesToAddRight+1];

        for (int y = 0; y <= numberOfTilesToAddRight; ++y) {
            for (int x = 0; x <= numberOfTilesToAddDown; ++x) {
                arrayOfTiles[x][y] = new TileManager.TileId(zoomLevel, indexX + x, indexY + y);
            }
        }
        return arrayOfTiles;
    }
}
