package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTestOur {


    //ne pas utiliser ce test.
    /*
    @Test
    void sectorInAreaTest(){
        ByteBuffer buffer = ByteBuffer.allocate(6*128*128);
        for (int i = 0; i < 6*128*128; i++) {
            buffer.put(new byte[]{1});

        }

        GraphSectors test = new GraphSectors(buffer);

        PointCh point = new PointCh(2485001, 1075001);

        test.sectorsInArea(point, 349000);
    } */


    @Test
    void sectorInAreaTestNumberOfSectors(){

        ByteBuffer buffer = ByteBuffer.allocate(6*128*128);
        GraphSectors graphSectors = new GraphSectors(buffer);


        PointCh point = new PointCh(2600000, 1200000);


        //ceci test le nombre de secteurs avec un rayon de 0, il devrait renvoyer qu'un seul secteur,
        List<GraphSectors.Sector> listeOfSectors = graphSectors.sectorsInArea(point, 0);

        listeOfSectors = graphSectors.sectorsInArea(point, 0);

        assertEquals(listeOfSectors.size(), 1); //Test OK

        //Test censé renvoyer tous les secteurs.

        listeOfSectors = graphSectors.sectorsInArea(point, SwissBounds.WIDTH);

        //ceci test le nombre de secteurs. (devrait renvoyer tous les secteurs)
        assertEquals(listeOfSectors.size(), 128*128); //TODO TEST PAS OK




        double x = SwissBounds.WIDTH / 128;
        double y = SwissBounds.HEIGHT / 128;

        //Point 1:
        PointCh point1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        //Point 2:
        PointCh point2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + y);
        //Point 3:
        PointCh point3 = new PointCh(SwissBounds.MIN_E+x, SwissBounds.MIN_N+y);
        //Point 6:
        PointCh point6 = new PointCh(SwissBounds.MIN_E+x, SwissBounds.MIN_N);
        //Point 4:
        PointCh point4 = new PointCh(SwissBounds.MIN_E+2.5*x,  SwissBounds.MIN_N+2.5*y);
        //Point 9:
        PointCh point9 = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);



        //Test de nombre de case:
        assertEquals(graphSectors.sectorsInArea(point1, y/2).size(), 1);

        assertEquals(graphSectors.sectorsInArea(point2, y/2).size(), 2);

        assertEquals(graphSectors.sectorsInArea(point3, y/2).size(), 4);

        assertEquals(graphSectors.sectorsInArea(point6, y/2).size(), 2);

        assertEquals(graphSectors.sectorsInArea(point4, x/2 +1).size(), 9);

        assertEquals(graphSectors.sectorsInArea(point9, y/2).size(), 1);

        //assertEquals(graphSectors.sectorsInArea(point3, 0).size(), 4);

    }

    @Test
    void testSectorsInArea(){

        ByteBuffer buffer = ByteBuffer.allocate(6*128*128);
        buffer.put(0, new byte[]{0});
        buffer.put(1, new byte[]{0});
        buffer.put(2, new byte[]{0});
        buffer.put(3, new byte[]{5});
        buffer.put(4, new byte[]{0});
        buffer.put(5, new byte[]{20});

        int i = 130*6;

        buffer.put(i, new byte[]{0});
        buffer.put(i+1, new byte[]{0});
        buffer.put(i+2, new byte[]{0});
        buffer.put(i+3, new byte[]{14});
        buffer.put(i+4, new byte[]{0});
        buffer.put(i+5, new byte[]{7});
        GraphSectors graphSectors = new GraphSectors(buffer);
        //Point 1:
        PointCh point1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        GraphSectors graphSectors1 = new GraphSectors(buffer);
        List <GraphSectors.Sector> liste = graphSectors1.sectorsInArea(point1,1);
        GraphSectors.Sector sector1 = liste.get(0);
        assertEquals(sector1.startNodeId() , 5);
        assertEquals(sector1.endNodeId(), 25);
        assertEquals(Math.abs(sector1.startNodeId()-sector1.endNodeId()), 20);

        PointCh point2 = new PointCh(SwissBounds.MIN_E + 2* SwissBounds.WIDTH/128, SwissBounds.MIN_N + 1.5* SwissBounds.HEIGHT/128);

        List <GraphSectors.Sector> liste2 = graphSectors1.sectorsInArea(point2,SwissBounds.HEIGHT/(128*4));
        assertEquals(liste2.size(), 2);
        assertEquals(liste2.get(1).startNodeId(), 14);
        assertEquals(liste2.get(1).endNodeId(), 14+7);




    }


}