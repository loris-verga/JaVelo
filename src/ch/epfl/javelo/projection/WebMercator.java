package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * La classe WebMercator qui est une classe publique, finale et non instantiable offre des méthodes statiques qui permettent de
 * convertir entre les coordonnées WGS84 et les coordonnées Web Mercator
 *
 * Les méthodes de la classe WebMercator ne valident pas leurs arguments. Ce travail est laissé aux classes représentant les points
 *
 * @author Loris Verga (345661)
 */
public final class WebMercator {

    /**
     * Cette méthode retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radian
     * @param lon longitude d'un point donnée en radiant
     * @return coordonnée x de la projection
     */
    public static double x(double lon){
        double x = (1/(2 * Math.PI)) * (lon+Math.PI);
        return x;
    }


    /**
     * Cette méthode retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radian
     * @param lat longitude d'un point donnée en radiant
     * @return coordonnée y de la projection
     */
    public static double y(double lat){
        double y = (1/(2 * Math.PI)) * (Math.PI- Math2.asinh(Math.tan(lat)));
        return y;
    }

    /**
     * Cette méthode retourne la longitude en radian d'un point dont la projection se trouve à la coordonnée x donnée.
     * @param x coordonnée x dans le système WebMercator
     * @return coordonnée longitude en radian dans le système WGS84
     */
    public static double lon(double x){
        double lambda = 2 * Math.PI * x - Math.PI;
        return lambda;
    }

    /**
     * Cette méthode retourne la latitude en radian d'un point dont la projection se trouve à la coordonnée y donnée.
     * @param y coordonnée y dans le système WebMercator
     * @return coordonnée latitude en radian dans le système WGS84
     */
    public static double lat(double y){
        double phi = Math.atan(Math.sinh(Math.PI-2 * Math.PI*y));
        return phi;
    }

}
