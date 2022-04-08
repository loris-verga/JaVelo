package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

/**
 * L'enregistrement RoutePoint représente le point d'un itinéraire le plus proche d'un point de référence donné,
 * qui se trouve dans le voisinage de l'itinéraire
 *
 * @param point               Le point sur l'itinéraire.
 * @param position            La position du point le long de l'itinéraire, en mètres.
 * @param distanceToReference La distance, en mètres, entre le point et la référence.
 * @author Loris Verga (345661)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public final static RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);


    /**
     * La méthode withPositionShiftedBy retourne un point identique au récepteur (this),
     * mais dont la position est décalée de la différence donnée, qui peut être positive ou négative.
     *
     * @param positionDifference différence à modifier
     * @return un nouveau RoutePoint avec la modification
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(new PointCh(point.e(), point.n()),
                position + positionDifference, distanceToReference);
    }

    /**
     * La méthode min retourne this si sa distance à la référence est inférieure ou égale à celle de that
     * et that sinon.
     *
     * @param that Un autre point.
     * @return ce point (this) ou l'autre (that) suivant lequel est le plus proche de la référence.
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference() <= that.distanceToReference() ? this : that;
    }


    /**
     * La méthode retourne this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon.
     *
     * @param thatPoint               Un autre RoutePoint
     * @param thatPosition            La nouvelle position pour créer l'instance
     * @param thatDistanceToReference La nouvelle distance pour créer la référence.
     * @return this ou la nouvelle instance de RoutePoint.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
