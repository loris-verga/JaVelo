package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestGPX {


    public static void main(String[] args) {

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

        int n = route.edges().size();

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, n);

        for (PointCh point : route.points()){
            System.out.println(point.lat() + "         " + point.lon());
        }

        Path path = Path.of("testGPX").resolve("z.xml");

        GpxGenerator.writeGpx(path.toString(), route, profile);



    }
}
