package ch.epfl.javelo.projection;


import ch.epfl.javelo.Preconditions;

import java.awt.*;

/**
 * Classe record PointWebMercator : Cette classe représente un point dans le système Web Mercator
 *
 * @author Loris Verga (345661)
 */
public record PointWebMercator(double x, double y) {

    /**
     * Le constructeur compact de la classe PointWebMercator valide les coordonnées qu'il reçoit
     * et lève une IllegalArgumentException si l'une d'entre elles n'est pas comprise dans l'intervalle [0;1].
     * @param x  double x, la coordonnée x du point
     * @param y double y, la coordonnée y du point
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x<= 1); //Vérification coordonnée x
        Preconditions.checkArgument(y >= 0 && x<= 1); //Vérification coordonnée y
    }


    /**
     * Méthode de construction qui retourne le point dont les coordonnées sont x et y au niveau de zoom zoomlevel
     * @param zoomLevel niveau de zoom
     * @param x coordonnée x du point dans le système WebMercator
     * @param y coordonnée y du point dans le système WebMercator
     * @return point WebMercator
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        double xPointWebMercator = Math.scalb(x, -(zoomLevel+8));
        double yPointWebMercator = Math.scalb(y, -(zoomLevel+8));
        return new PointWebMercator(xPointWebMercator, yPointWebMercator);
    }


    /**
     * Méthode de construction d'un point WebMercator
     * @param pointCh point dans le système suisse
     * @return  point dans le système PointWebMercator
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {

        //Récupération des coordonnées dans le système suisse
        double coord_est = pointCh.e();
        double coord_nord = pointCh.n();

        //Conversion dans le système WGS84
        double lon = Ch1903.lon(coord_est, coord_nord);
        double lat = Ch1903.lat(coord_est, coord_nord);

        //Conversion des coordonnées dans le système WebMercator
        double x = WebMercator.x(lon);
        double y = WebMercator.y(lat);

        //Création et retour du point
        return new PointWebMercator(x,y);
    }


    /**
     * Cette méthode retourne la coordonnée x au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @return coordonnée x (système WebMercator)
     */
    public double xAtZoomLevel(int zoomLevel){
        double x = this.x();
        return Math.scalb(x, 8+zoomLevel);
    }




    /**
     * Cette méthode retourne la coordonnée y au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @return coordonnée y (système WebMercator)
     */
    public double yAtZoomLevel(int zoomLevel){
        double y = this.y();
        return Math.scalb(y, 8+zoomLevel);
    }


    /**
     * Cette méthode retourne la longitude d'un point en radiant (système WGS84)
     * @return longitude en radiant
     */
    public double lon(){
        return WebMercator.lon(this.x());
    }

    /**
     * Cette méthode retourne la latitude d'un point en radian (système WGS84)
     * @return latitude en radiant
     */
    public double lat(){
        return WebMercator.lat(this.y());
    }

    /**
     * Cette méthode ui retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this)
     * ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds.
     * @return le point de coordonnée dans le système suisse (PointCh)
     */
    public PointCh toPointCh(){
        double longitude = this.lon();
        double latitude = this.lat();
        double e = Ch1903.e(longitude, latitude);
        double n = Ch1903.n(longitude, latitude);

        if (!(SwissBounds.containsEN(e,n))){
            return null;
        }
        else {
            return new PointCh(e,n);

        }
    }
}

