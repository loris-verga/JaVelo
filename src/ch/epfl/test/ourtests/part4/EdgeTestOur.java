package ch.epfl.test.ourtests.part4;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTestOur {
    private static final double DELTA = 1e-7;
    private static final double DELTA2 = 0;

    Edge creatorOfEdge(int eStart, int nStart, int eEnd, int nEnd){
        PointCh point1 = new PointCh(SwissBounds.MIN_E + eStart, SwissBounds.MIN_N + nStart);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + eEnd, SwissBounds.MIN_N + nEnd);
        double length = Math.hypot(eStart - eEnd, nEnd - nStart);
        DoubleUnaryOperator profil =  Functions.sampled(new float[]{0,0,0,0}, length);
        return new Edge(0,1, point1, point2, length ,profil);
    }

    @Test
    void positionClosestToOnKnownValuesAndLimitCases() {
        Edge edge1 = creatorOfEdge(6, 5, 18, 9);
        Edge edge2 = creatorOfEdge(10,13,6,25);

        assertEquals(Math.hypot(12 - 6, 7 - 5) ,edge1.positionClosestTo(new PointCh(SwissBounds.MIN_E + 10,SwissBounds.MIN_N + 13)), DELTA );
        assertEquals(Math.hypot(7.8 - 10 ,19.6 - 13) ,edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 8.4,SwissBounds.MIN_N + 19.8)), DELTA );
        assertEquals((-1)*Math.hypot(10 - 11.5,13 - 8.5) ,edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 13,SwissBounds.MIN_N + 9)), DELTA );
        assertEquals(Math.hypot(10 - 7.8, 13 - 19.6) ,edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 8.4,SwissBounds.MIN_N + 19.8)), DELTA );
        assertEquals(Math.hypot(10 - 5, 13 - 28) ,edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 11,SwissBounds.MIN_N + 30)), DELTA );

    }

    @Test
    void pointAtToOnKnownValuesAndLimitCases() {
        Edge edge1 = creatorOfEdge(6, 5, 18, 9);
        Edge edge2 = creatorOfEdge(10,13,6,25);

        assertEquals(new PointCh(SwissBounds.MIN_E + 12,SwissBounds.MIN_N + 7).e(), edge1.pointAt(Math.hypot(12 - 6, 7 - 5)).e(), DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 12,SwissBounds.MIN_N + 7).n(), edge1.pointAt(Math.hypot(12 - 6, 7 - 5)).n(), DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 7.8,SwissBounds.MIN_N + 19.6).e(), edge2.pointAt(Math.hypot(7.8 - 10 ,19.6 - 13)).e() ,DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 7.8,SwissBounds.MIN_N + 19.6).n(), edge2.pointAt(Math.hypot(7.8 - 10 ,19.6 - 13)).n() ,DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 11.5,SwissBounds.MIN_N + 8.5).e(), edge2.pointAt((-1)*Math.hypot(10 - 11.5,13 - 8.5)).e() ,DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 11.5,SwissBounds.MIN_N + 8.5).n(), edge2.pointAt((-1)*Math.hypot(10 - 11.5,13 - 8.5)).n() ,DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 7.8,SwissBounds.MIN_N + 19.6).e(), edge2.pointAt(Math.hypot(10 - 7.8, 13 - 19.6)).e() , DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 7.8,SwissBounds.MIN_N + 19.6).n(), edge2.pointAt(Math.hypot(10 - 7.8, 13 - 19.6)).n() , DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 5,SwissBounds.MIN_N + 28).e(), edge2.pointAt(Math.hypot(10 - 5, 13 - 28)).e() , DELTA2 );
        assertEquals(new PointCh(SwissBounds.MIN_E + 5,SwissBounds.MIN_N + 28).n(), edge2.pointAt(Math.hypot(10 - 5, 13 - 28)).n() , DELTA2 );

    }
}