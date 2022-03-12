package ch.epfl.test;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTestOur {

    public static final double DELTA = 1e-7;

    @Test
    void constructionClassique(){
        PointWebMercator point = new PointWebMercator(0.2, 1);
    }

    @Test
    void constructorThrowException(){
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator point = new PointWebMercator(1.2, 1);
        });
    }

    @Test
    void constructorThrowException2(){
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator point = new PointWebMercator(1, -1);
        });
    }

    @Test
    void ofWorksOnNonTrivialValue(){
        double x_19 = 69561722;
        double y_19 = 47468099;
        int zoomlevel = 19;
        PointWebMercator calculated = PointWebMercator.of(zoomlevel, x_19, y_19);
        PointWebMercator expected = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(calculated.x(), expected.x(), DELTA);
        assertEquals(calculated.y(), expected.y(), DELTA);
    }
    //Peut-être faudrait-il tester avec des valeurs plus triviales

    @Test
    void ofPointChWorksOnNonTrivialValue(){
        double lambda = Math.toRadians(6.5790772);
        double phi = Math.toRadians(46.5218976);
        double e = Ch1903.e(lambda, phi);
        double n = Ch1903.n(lambda, phi);
        PointCh point = new PointCh(e, n);
        PointWebMercator calculated = PointWebMercator.ofPointCh(point);
        PointWebMercator expected = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(calculated.x(), expected.x(), DELTA);
        System.out.println(calculated.x());
        System.out.println(expected.x());
        assertEquals(calculated.y(), expected.y(), DELTA);
    }

    @Test
    void xAtZoomLevelWorksOnNonTrivialValue(){
        double x = 0.518275214444;
        double y = 0.353664894749;
        int zoom = 19;
        double expected = 47468099;
        PointWebMercator point = new PointWebMercator(x, y);
        assertEquals(expected, point.yAtZoomLevel(zoom), 1/DELTA);
    }
    @Test
    void yAtZoomLevelWorksOnNonTrivialValue(){
        double x = 0.518275214444;
        double y = 0.353664894749;
        int zoom = 19;
        double expected = 69561722;
        PointWebMercator point = new PointWebMercator(x, y);
        assertEquals(expected, point.xAtZoomLevel(zoom) , 1/DELTA);
    }

    @Test
    void lonAndLatWorksOnNonTrivialValue(){
        double lambda = Math.toRadians(6.5790772);
        double phi = Math.toRadians(46.5218976);
        double e = Ch1903.e(lambda, phi);
        double n = Ch1903.n(lambda, phi);
        PointCh point = new PointCh(e, n);
        PointWebMercator calculated = PointWebMercator.ofPointCh(point);
        assertEquals(lambda, calculated.lon(), DELTA*100);
        assertEquals(phi, calculated.lat(), DELTA*100);
    }

    @Test
    void toPointChWorksOnTrivialValue(){
        PointWebMercator a = new PointWebMercator(0,0);
        assertEquals(null, a.toPointCh());
    }

    @Test
    void toPointChWorksOnNonTrivialValue(){
        double lambda = Math.toRadians(6.5790772);
        double phi = Math.toRadians(46.5218976);
        double e = Ch1903.e(lambda, phi);
        double n = Ch1903.n(lambda, phi);
        PointCh point = new PointCh(e, n);
        PointWebMercator calc1 = new PointWebMercator(0.518275214444, 0.353664894749);
        PointCh calc2 = calc1.toPointCh();
        assertEquals(point.e(), calc2.e(), DELTA*1000);
        assertEquals(point.n(), calc2.n(), DELTA*1000);
    }
}

