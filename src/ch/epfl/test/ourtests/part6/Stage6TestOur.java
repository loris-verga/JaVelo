package ch.epfl.test.ourtests.part6;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class Stage6TestOur {

        public static void main(String[] args) throws IOException {
            Graph g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            RouteComputerTester rcTest = new RouteComputerTester(g,cf);
            long t0 = System.nanoTime();
            Route r = rc.bestRouteBetween(0,1050);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
            RouteComputerTestOur.KmlPrinter.write("javelo.kml", r);
            System.out.println(r.length());
            }
        }

