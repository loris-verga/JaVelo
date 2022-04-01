package ch.epfl.test.ourtests.part6;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import ch.epfl.test.ourtests.part5.RouteComputerTestOur;

import java.io.IOException;
import java.nio.file.Path;

public class Stage6TestOur {
        public static void main(String[] args) throws IOException {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = rc.bestRouteBetween(0, 99999);
            RouteComputerTestOur.KmlPrinter.write("javelo.kml", r);
        }
    }

