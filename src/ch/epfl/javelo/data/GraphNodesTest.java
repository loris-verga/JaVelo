package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    @Test
    void graphNodesConstructorWorks(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void graphNodesTest2(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(2485000), Q28_4.ofInt(2834000),0x2_000_1234, Q28_4.ofInt(2485000), Q28_4.ofInt(2834000),0x2_000_1234,Q28_4.ofInt(2485000), Q28_4.ofInt(2834000),0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
    }

    @Test
    void graphNodesTest3(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(0),
                Q28_4.ofInt(0),
                0x2_000_1234,

        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
    }

    @Test
    void graphNodesTest4(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(2485000),
                Q28_4.ofInt(2834000),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2485000,ns.nodeE(0));
    }
    @Test
    void graphNodesTest5(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(0),
                Q28_4.ofInt(2834000),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(0,ns.nodeE(0));

    }

    @Test
    void graphNodesTest6(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(283400, ns.nodeN(0));
    }

    @Test
    void graphNodesTest7(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234,

                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234,

                Q28_4.ofInt(2487677),
                Q28_4.ofInt(2400000),
                0x2_000_1234,

                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2400000, ns.nodeN(2));

    }

    @Test
    void graphNodes8(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.outDegree(0));

    }

    @Test
    void graphNodes9(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234,

                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234,

                Q28_4.ofInt(2487677),
                Q28_4.ofInt(2400000),
                0xA_000_1234,

                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(10 , ns.outDegree(2));
    }


    @Test
    void graphNodes10(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(248677),
                Q28_4.ofInt(283400),
                0x2_000_1234,
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertThrows(IllegalArgumentException.class, () -> {
            ns.edgeId(0, 6);

        });
    }








}