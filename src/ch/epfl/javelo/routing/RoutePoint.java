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


    /**
     * Cette méthode retourne un point identique au récepteur (this),
     * mais dont la position est décalée de la différence donnée, qui peut être positive ou négative
     * @param positionDifference différence à modifier
     * @return un nouveau RoutePoint avec la modification
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(new PointCh(point.e(), point.n()), position + positionDifference, distanceToReference);
    }

    /**
     * Cette méthode retourne this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon
     * @param that autre point
     * @return ce point (this) ou l'autre (that) suivant lequel est le plus proche de la référence
     */
    public RoutePoint min(RoutePoint that){
        if (this.distanceToReference()<= that.distanceToReference()){
            return this;
        }
        return that;
    }


    /**
     * Cette méthode retourne this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon.
     * @param thatPoint un autre RoutePoint
     * @param thatPosition la nouvelle position pour créer l'instance
     * @param thatDistanceToReference la nouvelle distance pour créer la référence.
     * @return this ou la nouvelle instance de RoutePoint
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if (this.distanceToReference<=thatDistanceToReference){
            return this;
        }
        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
