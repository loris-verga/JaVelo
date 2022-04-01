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
            Route r = rc.bestRouteBetween(140000,1050);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
            RouteComputerTestOur.KmlPrinter.write("javelo.kml", r);
            System.out.println(r.length());

            for(int startNode = 0; startNode + 1000 < g.nodeCount(); startNode += 100){
                    Route actualRoute = rc.bestRouteBetween(startNode, startNode + 1000);
                    Route expectedRoute = rcTest.bestRouteBetween(startNode, startNode + 1000);
                    if(actualRoute != null){
                        if(actualRoute.length() != expectedRoute.length()){
                            System.out.println("actual =" + actualRoute.length());
                            System.out.println("expected =" + expectedRoute.length());
                        }
                    //    for(int edgeIndex = 0; edgeIndex < actualRoute.edges().size() ; edgeIndex+=10) {
                    //        if (actualRoute.edges().get(edgeIndex).equals(expectedRoute.edges().get(edgeIndex))) {
                    //            System.out.println("true");
                    //        }

                    }
                }
            }
        }

