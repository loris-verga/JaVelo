package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

/**
 * L'enregistrement RoutePoint représente le point d'un itinéraire le plus proche d'un point de référence donné,
 * qui se trouve dans le voisinage de l'itinéraire
 *
 * @author Loris Verga (345661)
 *
 *
 * @param point le point sur l'itinéraire
 * @param position la position du point le long de l'itinéraire, en mètres
 * @param distanceToReference la distance, en mètres, entre le point et la référence
 */
public record RoutePoint(PointCh point, double position, double distanceToReference){

    public final static RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);


}
