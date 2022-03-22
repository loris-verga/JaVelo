package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    @Test
    void SingleTest(){
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
        List <Edge> edges3 = uneRoute.edges();
        assertEquals(edges3.size(), edgesList.size());
        for (int i = 0; i<edges3.size(); ++i){
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

        List<PointCh> listOfPoints= new ArrayList<>();
        listOfPoints.add(p1);
        listOfPoints.add(p2);
        listOfPoints.add(p3);
        listOfPoints.add(p4);
        List<PointCh> listObtained = une2Route.points();
        assertEquals(listOfPoints.size(), listObtained.size());
        for (int i = 0; i<listOfPoints.size(); ++i){
            assertEquals(listOfPoints.get(i).e(), listObtained.get(i).e());
            assertEquals(listOfPoints.get(i).n(), listObtained.get(i).n());
        }


        //TODO pointAt

        //TODO nodeClosestTo

        //TODO elevationAt

        //TODO pointClosestTo

        //TODO Rendu



















    }

}