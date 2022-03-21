package ch.epfl.test;

import ch.epfl.javelo.data.AttributeSet;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.*;

class GraphTestOur {

    @Test
    void loadFromTest1() throws IOException {
        Path basePathOfLausanne = Path.of("lausanne");
        Graph aGraph = Graph.loadFrom(basePathOfLausanne);
    }



    @Test
    void test2() throws IOException{
        Path basePathOfLausanne = Path.of("lausanne");
        Graph aGraph = Graph.loadFrom(basePathOfLausanne);
        int id = aGraph.nodeClosestTo(new PointCh(2538612, 1153353), 30);
        System.out.println(id);
        System.out.println(aGraph.nodePoint(id).e() + "   " + aGraph.nodePoint(id).n());
        assertEquals(aGraph.nodeOutDegree(id), 2);
        int index = aGraph.nodeOutEdgeId(id, 0);
        AttributeSet attributSet = aGraph.edgeAttributes(index);
        System.out.println(attributSet);
        System.out.println(aGraph.edgeLength(index));
        System.out.println(aGraph.edgeElevationGain(index));
        System.out.println(aGraph.edgeProfile(index).applyAsDouble(5));





    }


}