package ch.epfl.test.ourtests.part6;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTestOur {

    @Test
    void test(){

        List<Route> listeVide = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            Route uneRoute = new MultiRoute(listeVide);
        });


        var p1 = new PointCh(2600_000, 1200_000);
        var p2 = new PointCh(2600_100, 1200_000);
        var p3 = new PointCh(2600_200, 1200_000);
        var p4 = new PointCh(2600_300, 1200_000);

        var p5 = new PointCh(2600_400, 1200_000);
        var p6 = new PointCh(2600_500, 1200_000);
        var p7 = new PointCh(2600_600, 1200_000);
        var p8 = new PointCh(2600_700, 1200_000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x->Double.NaN);
        var edge2 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Double.NaN);
        var edge3 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        var edge4 = new Edge(4, 5, p4, p5, p2.distanceTo(p3), x -> Double.NaN);

        var edge5 = new Edge(5, 6, p5, p6, p5.distanceTo(p6), Functions.constant(5));
        var edge6 = new Edge(6, 7, p6, p7, p6.distanceTo(p7), Functions.constant(5));
        var edge7 = new Edge(7, 8, p7, p8, p7.distanceTo(p8), Functions.constant(5));

        List<Edge> listOfEdge1 = new ArrayList<>();
        List<Edge> listOfEdge2 = new ArrayList<>();

        listOfEdge1.add(edge1);
        listOfEdge1.add(edge2);
        listOfEdge1.add(edge3);
        listOfEdge1.add(edge4);

        listOfEdge2.add(edge5);
        listOfEdge2.add(edge6);
        listOfEdge2.add(edge7);

        SingleRoute route1 = new SingleRoute(listOfEdge1);
        SingleRoute route2 = new SingleRoute(listOfEdge2);

        List <Route> listOfSegments = new ArrayList<>();
        listOfSegments.add(route1);
        listOfSegments.add(route2);

        MultiRoute route = new MultiRoute(listOfSegments);

        List<Edge> listOfAllEdges = new ArrayList<>();
        listOfAllEdges.add(edge1);
        listOfAllEdges.add(edge2);
        listOfAllEdges.add(edge3);
        listOfAllEdges.add(edge4);
        listOfAllEdges.add(edge5);
        listOfAllEdges.add(edge6);
        listOfAllEdges.add(edge7);


        assertEquals(700.0, route.length());

        List<Edge> computedListOfEdge = route.edges();
        assertEquals(computedListOfEdge.size(), listOfAllEdges.size());
        for (int i = 0; i<computedListOfEdge.size(); ++i){
            assertEquals(computedListOfEdge.get(i), listOfAllEdges.get(i));
        }

        List<PointCh> listOfAllPoints = new ArrayList<>();
        listOfAllPoints.add(p1);
        listOfAllPoints.add(p2);
        listOfAllPoints.add(p3);
        listOfAllPoints.add(p4);
        listOfAllPoints.add(p5);
        listOfAllPoints.add(p6);
        listOfAllPoints.add(p7);
        listOfAllPoints.add(p8);

        assertEquals(listOfAllPoints.size(), route.points().size());

        for(int i = 0; i<listOfAllPoints.size(); ++i){
            assertEquals(listOfAllPoints.get(i), route.points().get(i));
        }


        assertEquals(0, route.indexOfSegmentAt(0));
        assertEquals(0, route.indexOfSegmentAt(-100));
        assertEquals(1, route.indexOfSegmentAt(10000));
        assertEquals(0, route.indexOfSegmentAt(200));
        assertEquals(1, route.indexOfSegmentAt(401));
        assertEquals(1, route.indexOfSegmentAt(400));
        assertEquals(0, route.indexOfSegmentAt(399));


        assertEquals(new PointCh(2600000, 1200000), route.pointAt(0));
        assertEquals(new PointCh(2600001, 1200000), route.pointAt(1));
        assertEquals(new PointCh(2600000, 1200000), route.pointAt(-100));
        assertEquals(new PointCh(2600665, 1200000), route.pointAt(665));
        assertEquals(new PointCh(2600700, 1200000), route.pointAt(100000));

        assertEquals(1, route.nodeClosestTo(0));
        assertEquals(1, route.nodeClosestTo(1));
        assertEquals(1, route.nodeClosestTo(49));
        assertEquals(1, route.nodeClosestTo(50));
        assertEquals(2, route.nodeClosestTo(51));
        assertEquals(7, route.nodeClosestTo(643));
        assertEquals(8, route.nodeClosestTo(1000000));




        Assertions.assertEquals(new RoutePoint(p1, 0, 0), route.pointClosestTo(p1));
        assertEquals(new RoutePoint(p1, 0, 3), route.pointClosestTo(new PointCh(2600000-3, 1200000)));
        assertEquals(new RoutePoint(p1, 0, 70), route.pointClosestTo(new PointCh(2600000-70, 1200000)));
        assertEquals(new RoutePoint(p2, 100, 0), route.pointClosestTo(p2));
        assertEquals(new RoutePoint(new PointCh(2600_665, 1200000), 665, 100), route.pointClosestTo(new PointCh(2600_665, 1200100)));
        assertEquals(new RoutePoint(p8, 700, 1000), route.pointClosestTo(new PointCh(2601700, 1200000)));




        assertEquals(Double.NaN, route.elevationAt(-100));
        assertEquals(Double.NaN, route.elevationAt(0));
        assertEquals(Double.NaN, route.elevationAt(99));
        assertEquals(Double.NaN, route.elevationAt(100));
        assertEquals(Double.NaN, route.elevationAt(399));
        assertEquals(5, route.elevationAt(400));
        assertEquals(5, route.elevationAt(401));
        assertEquals(5, route.elevationAt(10000));

    }
}