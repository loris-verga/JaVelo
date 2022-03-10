package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {


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

        //Liste de valeurs utiles pour les tests

        //la plus petite coordonnée E de Suisse
        double MIN_E = 2485000;

        //la plus grande coordonnée E de Suisse
        double MAX_E = 2834000;

        //la plus petite coordonnée N de Suisse
        double MIN_N = 1075000;

        //la plus grande coordonnée N de Suisse
        double MAX_N = 1296000;

        //la largeur de la Suisse en mètres, définie comme la différence entre MAX_E et MIN_E
        double WIDTH = MAX_E-MIN_E;




        ByteBuffer buffer = ByteBuffer.allocate(6*128*128);
        GraphSectors graphSectors = new GraphSectors(buffer);


        PointCh point = new PointCh(2600000, 1200000);


        //ceci test le nombre de secteurs avec un rayon de 0, il devrait renvoyer qu'un seul secteur,
        List<GraphSectors.Sector> listeOfSectors = graphSectors.sectorsInArea(point, 0);

        listeOfSectors = graphSectors.sectorsInArea(point, 0);

        assertEquals(listeOfSectors.size(), 1); //Test OK

        //Test censé renvoyer tous les secteurs.

        listeOfSectors = graphSectors.sectorsInArea(point, WIDTH);

        //ceci test le nombre de secteurs. (devrait renvoyer tous les secteurs)
        assertEquals(listeOfSectors.size(), 128*128); //TODO TEST PAS OK














    }


}