package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId ){

        //todo verify that everything here is correct
        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profile = graph.edgeProfile(edgeId);

        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);
    }

    public double positionClosestTo(PointCh point){
        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();
        double pY = point.n();
        return Math2.projectionLength(aX, aY, bX, bY, pX, pY);
    }

    public PointCh pointAt(double position){
        //todo check if works if values further away from fromPoint and toPoint
        double eCoordinate = (toPoint.e() - fromPoint.e()) * position/length + fromPoint.e();
        double nCoordinate = (toPoint.n() - fromPoint.n()) * position/length + fromPoint.n();
        return new PointCh(eCoordinate, nCoordinate);
    }

    public double elevationAt(double position){
        PointCh newPoint = pointAt(position);
        //todo have no idea how to do ask help...
        return 0.0;
    }

}
