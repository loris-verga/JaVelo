package ch.epfl.test.ourtests.part2;

import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test de WebMercator
 *
 * @author Loris Verga (345661)
 */
class WebMercatorTestOur {

    public static final double DELTA = 1e-7;

    @Test
    void xWorksOnTrivialValues(){
        double lambda = 0;
        double expected = 0.5;
        Assertions.assertEquals(expected, WebMercator.x(lambda), DELTA);
    }
    @Test
    void xWorksOnNonTrivialValues(){
        double lambda = 1;
        double expected = 0.6591549430918953357688837633725143620344596457404564487476673440;
        assertEquals(expected, WebMercator.x(lambda), DELTA);
    }

    @Test
    void yWorksOnTrivialValue(){
        double lambda = 0;
        double expected = 0.5;
        assertEquals(expected, WebMercator.y(lambda));
    }

    @Test
    void yWorksOnNonTrivialValue(){
        double lambda = 1;
        double expected = 0.3048456139782493315900387887828304048521579474454390958147153151;
    assertEquals(expected, WebMercator.y(lambda), DELTA);
    }

    @Test
    void lonWorksOnTrivialValue(){
        double x = 0;
        double expected = -3.141592653589793238462643383279502884197169399375105820974944592;
        assertEquals(expected, WebMercator.lon(x), DELTA);
    }

    @Test
    void lonWorksOnNonTrivialValue(){
        double x = 5;
        double expected = 28.274333882308139146163790449515525957774524594375952388774501330;
        assertEquals(expected, WebMercator.lon(x));
    }

    @Test
    void latWorksOnTrivialValue(){
        double y = 0.5;
        double expected = 0;
        assertEquals(expected, WebMercator.lat(y));
    }

    @Test
    void latWorksOnNonTrivialValue(){
        double y = 1;
        double expected = -1.484422229745332366961096793966038538621707926660881407816382467;
        assertEquals(expected, WebMercator.lat(y));
    }

}