package ch.epfl.javelo.projection;

/**
 * La classe WebMercator qui est une classe publique, finale et non instanciable offre des méthodes statiques qui permettent de
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
    public double x(double lon){
        //TODO à remplir
    }

    /**
     * Cette méthode retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radian
     * @param lat longitude d'un point donnée en radiant
     * @return coordonnée y de la projection
     */
    public double y(double lat){
        //TODO à remplir

    }

    /**
     * Cette méthode retourne la longitude en radian d'un point dont la projection se trouve à la coordonnée x donnée.
     * @param x coordonnée x dans le système WebMercator
     * @return coordonnée longitude en radian dans le système WGS84
     */
    public double lon(double x){
        //TODO à remplir
    }

    /**
     * Cette méthode retourne la latitude en radian d'un point dont la projection se trouve à la coordonnée y donnée.
     * @param y coordonnée y dans le système WebMercator
     * @return coordonnée latitude en radian dans le système WGS84
     */
    public double lat(double y){
        //TODO à remplir
    }

}
