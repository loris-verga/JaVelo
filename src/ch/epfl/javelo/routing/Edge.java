package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;


/**
 * L'enregistrement Edge représente une arête le long d'un itinéraire.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Le constructeur d'une instance de Edge.
     *
     * @param graph      le graphe ou se situe l'arête.
     * @param edgeId     l'identité de l'arête.
     * @param fromNodeId l'identité du nœud depuis lequel l'arête commence.
     * @param toNodeId   l'identité du nœud depuis lequel l'arête termine.
     * @return une instance d'un objet de type Edge.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {

        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profile = graph.edgeProfile(edgeId);

        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);
    }

    /**
     * La méthode positionClosestTo retourne les coordonnées du point qui se situe le plus proche du point donné, le long de l'arête.
     *
     * @param point Le point donné.
     * @return la coordonnée du point se situant le plus proche du point donné, le long de l'arête.
     */
    public double positionClosestTo(PointCh point) {

        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();
        double pY = point.n();

        return Math2.projectionLength(aX, aY, bX, bY, pX, pY);
    }

    /**
     * La méthode pointAt retourne le point qui se situe a une distance position depuis le début de l'arête, sur l'arête.
     *
     * @param position La distance donnée.
     * @return le point qui se situe a une distance position depuis le début de l'arête, sur l'arête.
     */
    public PointCh pointAt(double position) {

        double eCoordinate = Math.fma(
                toPoint.e() - fromPoint.e(),
                position / length,
                fromPoint.e());

        double nCoordinate = Math.fma(
                toPoint.n() - fromPoint.n(),
                position / length,
                fromPoint.n());

        return new PointCh(eCoordinate, nCoordinate);
    }

    /**
     * La méthode elevationAt retourne l'altitude à une position donnée sur l'arête.
     *
     * @param position La position à laquelle on veut connaître l'altitude.
     * @return l'altitude à une position donnée sur l'arête.
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }

}
