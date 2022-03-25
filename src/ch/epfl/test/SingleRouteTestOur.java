package ch.epfl.test;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.RoutePoint;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTestOur {

    @Test
    void SingleTest() {
        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        var p3 = new PointCh(2600789, 1200123);
        var p4 = new PointCh(2601000, 1201000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        var edge2 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        List<Edge> edgesList = new ArrayList<>();
        edgesList.add(edge1);
        edgesList.add(edge2);

        SingleRoute uneRoute = new SingleRoute(edgesList);
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute uneAutreRoute = new SingleRoute(new ArrayList<>());
        });

        /*
        double [] tableau = uneRoute.positionArray;
        double [] unAutreTableau = tableau.clone();
        Arrays.sort(unAutreTableau);
        assertArrayEquals(tableau, unAutreTableau);*/

        assertEquals(0, uneRoute.indexOfSegmentAt(758917));
        assertEquals(p1.distanceTo(p2) + p3.distanceTo(p4), uneRoute.length());
        List<Edge> edges3 = uneRoute.edges();
        assertEquals(edges3.size(), edgesList.size());
        for (int i = 0; i < edges3.size(); ++i) {
            Edge e1 = edgesList.get(i);
            Edge e2 = edges3.get(i);
            assertEquals(e1, e2);
            assertEquals(e1.fromNodeId(), e2.fromNodeId());
            assertEquals(e1.length(), e2.length());
            assertEquals(e1.elevationAt(5), e2.elevationAt(5));
        }


        var edge3 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        var edge4 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Double.NaN);
        var edge5 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        ArrayList<Edge> edgesList2 = new ArrayList<>();
        edgesList2.add(edge3);
        edgesList2.add(edge4);
        edgesList2.add(edge5);
        SingleRoute une2Route = new SingleRoute(edgesList2);

        List<PointCh> listOfPoints = new ArrayList<>();
        listOfPoints.add(p1);
        listOfPoints.add(p2);
        listOfPoints.add(p3);
        listOfPoints.add(p4);
        List<PointCh> listObtained = une2Route.points();
        assertEquals(listOfPoints.size(), listObtained.size());
        for (int i = 0; i < listOfPoints.size(); ++i) {
            assertEquals(listOfPoints.get(i).e(), listObtained.get(i).e());
            assertEquals(listOfPoints.get(i).n(), listObtained.get(i).n());

        }

        var p10 = new PointCh(2600_000, 1200_000);
        var p11 = new PointCh(2600_100, 1200_100);
        var p12 = new PointCh(2600_200, 1200_200);

        var edge10 = new Edge(1, 2, p10, p11, p10.distanceTo(p11), x -> Double.NaN);
        var edge11 = new Edge(2, 3, p11, p12, p11.distanceTo(p12), x -> Double.NaN);
        ArrayList<Edge> edgeList3 = new ArrayList<>();
        edgeList3.add(edge10);
        edgeList3.add(edge11);
        SingleRoute une3Route = new SingleRoute(edgeList3);
        assertEquals(new PointCh(2600050, 1200050), une3Route.pointAt(p10.distanceTo(p11) * 0.5));
        double y = p10.distanceTo(p11) + p11.distanceTo(p12) * 0.75;
        assertEquals(new PointCh(2600175, 1200175), une3Route.pointAt(y));

        assertEquals(1, une3Route.nodeClosestTo(0));
        assertEquals(2, une3Route.nodeClosestTo(p10.distanceTo(p11) + 1));
        assertEquals(2, une3Route.nodeClosestTo(p10.distanceTo(p11) * 0.75));
        assertEquals(1, une3Route.nodeClosestTo(p10.distanceTo(p11) * 0.49));
        assertEquals(3, une3Route.nodeClosestTo(1000000));


    }

    @Test
    void testElevationAt() {
        /*var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        var p3 = new PointCh(2600789, 1200123);
        var p4 = new PointCh(2601000, 1201000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        var edge2 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        List<Edge> edgesList = new ArrayList<>();
        edgesList.add(edge1);
        edgesList.add(edge2);
        SingleRoute uneRoute = new SingleRoute(edgesList);
        //assertEquals(Double.NaN, uneRoute.elevationAt(0));
        //assertEquals(Double.NaN, uneRoute.elevationAt(1000000000));
        //assertEquals(Double.NaN, uneRoute.elevationAt(-5)); */
        var p5 = new PointCh(2600000, 1200000);
        var p6 = new PointCh(2600100, 1200000);
        var p7 = new PointCh(2600200, 1200000);
        var p8 = new PointCh(2601000, 1201000);
        float[] tableau = {0.f, 100.f};
        var edge3 = new Edge(1, 2, p5, p6, p5.distanceTo(p6), x -> Double.NaN);
        var edge4 = new Edge(3, 4, p6, p7, p6.distanceTo(p7), Functions.sampled(tableau, 100));
        List<Edge> edgesList2 = new ArrayList<>();
        edgesList2.add(edge3);
        edgesList2.add(edge4);
        SingleRoute uneRoute2 = new SingleRoute(edgesList2);
        /*assertEquals(5, uneRoute2.elevationAt(0));
        assertEquals(5, uneRoute2.elevationAt(3));
        assertEquals(5, uneRoute2.elevationAt(50));
        assertEquals(5, uneRoute2.elevationAt(100));
        assertEquals(Double.NaN, uneRoute2.elevationAt(101));
        assertEquals(Double.NaN, uneRoute2.elevationAt(100000));*/

        assertEquals(Math2.interpolate(0, 100, 0.5), uneRoute2.elevationAt(150));
        //assertEquals(5, uneRoute.elevationAt(200));

    }

    @Test
    void pointClosestToTest(){

        var p10 = new PointCh(2600_000, 1200_000);
        var p11 = new PointCh(2600_100, 1200_000);
        var p12 = new PointCh(2600_200, 1200_000);

        var edge10 = new Edge(1, 2, p10, p11, p10.distanceTo(p11), x -> Double.NaN);
        var edge11 = new Edge(2, 3, p11, p12, p11.distanceTo(p12), x -> Double.NaN);
        ArrayList<Edge> edgeList3 = new ArrayList<>();
        edgeList3.add(edge10);
        edgeList3.add(edge11);
        SingleRoute une3Route = new SingleRoute(edgeList3);

        Assertions.assertEquals(new RoutePoint(new PointCh(2600_050, 1200_000), 50, 3), une3Route.pointClosestTo(new PointCh(2600_050, 1200_000-3)));
        assertEquals(new RoutePoint(new PointCh(2600_150, 1200_000), 150, 67), une3Route.pointClosestTo(new PointCh(2600_150, 1200_000+67)));



    }

}