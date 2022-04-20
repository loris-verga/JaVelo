package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;



/**
 * L'enregistrement MapViewParameters représente les paramètres du fond de carte présenté dans l'interface
 * graphique. Il possède trois attributs qui sont :
 * @param zoomLevel le niveau de zoom
 * @param minX la coordonnée x du coin haut-gauche de la portion de carte affichée (Web Mercator)
 * @param minY la coordonnée y du coin haut-gauche de la portion de carte affichée (Web Mercator)
 */
public record MapViewParameters(int zoomLevel, double minX, double minY) {


    /**
     * La méthode topLeft retourne les coordonnées du coin haut-gauche du fond de carte sous
     * la forme d'un objet de type Point2D.
     * @return un Point2D représentant le point en haut à gauche.
     */
    public Point2D topLeft(){
        PointWebMercator point = PointWebMercator.of(zoomLevel, minX, minY);
        double lon = Math.toDegrees(point.lon());
        double lat = Math.toDegrees(point.lat());
        return new Point2D(lon, lat);
        }

    /**
     * La méthode withMinXY retourne une instance de MapViewParameters identique au récepteur
     * à l'exception que les coordonnées du coin haut-gauche sont celles passées en argument.
     * @param minX coordonnée x du coin haut-gauche.
     * @param minY coordonnée y du coin haut-gauche.
     * @return une nouvelle instance de MapViewParameters modifiée.
     */
    public MapViewParameters withMinXY(double minX, double minY){
        return new MapViewParameters(zoomLevel, minX, minY );
    }


    /**
     * La méthode pointAt prend en arguments les coordonnées d'un point exprimées par rapport
     * au coin haut-gauche de la portion de carte affichée à l'écran et retourne ce point.
     * @param x coordonnée x par rapport au coin haut-gauche
     * @param y coordonnée y par rapport au coin haut-gauche.
     * @return un point
     */
    public PointWebMercator pointAt(double x, double y){
        return PointWebMercator.of(zoomLevel, x+minX, y+minY);
    }

    /**
     * La méthode viewX prend en argument un point WebMercator et retourne la position x
     * correspondante, exprimée par rapport au coin haut-gauche.
     * @param point le point WebMercator.
     * @return la coordonnée x.
     */
    public double viewX(PointWebMercator point){
        return point.xAtZoomLevel(zoomLevel)-minX;
    }

    /**
     * La méthode viewY prend en argument un point WebMercator et retourne la position y
     * correspondante, exprimée par rapport au coin haut-gauche.
     * @param point le point WebMercator
     * @return la coordonnée y.
     */
    public double viewY(PointWebMercator point){
        return point.yAtZoomLevel(zoomLevel)-minY;
    }








}
