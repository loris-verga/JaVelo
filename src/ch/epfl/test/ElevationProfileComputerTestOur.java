package ch.epfl.test;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTestOur {

    @Test
    void elevationProfileThrowOnNegativeLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            float[] Tab1 = {0.f, 0.f};
            ElevationProfile actual1 = ElevationProfileComputer.elevationProfile(new testRoute(800, Tab1, 800), 0);
        });
    }

    @Test
    void elevationProfileOnKnownValues1() {
        float[] Tab1 = {0.f, 0.f};
        ElevationProfile actual1 = ElevationProfileComputer.elevationProfile(new testRoute(800, Tab1, 800), 800);
        ElevationProfile expected1 = new ElevationProfile(800, new float[]{0.f, 0.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected1.elevationAt(actual1.length() / i), actual1.elevationAt(actual1.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues2() {
        float[] Tab2 = {1.f, 2.f, 3.f, 4.f, 5.f, 6.f};
        ElevationProfile actual2 = ElevationProfileComputer.elevationProfile(new testRoute(500, Tab2, 100), 100);
        ElevationProfile expected2 = new ElevationProfile(500, new float[]{1.f, 2.f, 3.f, 4.f, 5.f, 6.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected2.elevationAt(actual2.length() / i), actual2.elevationAt(actual2.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues3() {
        float[] Tab3 = {Float.NaN, 2.f, 3.f, Float.NaN, 5.f, Float.NaN};
        ElevationProfile actual3 = ElevationProfileComputer.elevationProfile(new testRoute(500, Tab3, 100), 100);
        ElevationProfile expected3 = new ElevationProfile(500, new float[]{2.f, 2.f, 3.f, 4.f, 5.f, 5.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected3.elevationAt(actual3.length() / i), actual3.elevationAt(actual3.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues4() {
        float[] Tab4 = {Float.NaN, 2.f, 3.f, Float.NaN, 5.f, Float.NaN};
        ElevationProfile actual4 = ElevationProfileComputer.elevationProfile(new testRoute(500, Tab4, 100), 100);
        ElevationProfile expected4 = new ElevationProfile(500, new float[]{2.f, 2.f, 3.f, 4.f, 5.f, 5.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected4.elevationAt(actual4.length() / i), actual4.elevationAt(actual4.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues5() {
        float[] Tab5 = {1.f, Float.NaN, Float.NaN, Float.NaN, Float.NaN, 6.f};
        ElevationProfile actual5 = ElevationProfileComputer.elevationProfile(new testRoute(500, Tab5, 100), 100);
        ElevationProfile expected5 = new ElevationProfile(500, new float[]{1.f, 2.f, 3.f, 4.f, 5.f, 6.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected5.elevationAt(actual5.length() / i), actual5.elevationAt(actual5.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues6() {
        float[] Tab6 = {Float.NaN, 2.f, Float.NaN, Float.NaN, 5.f, Float.NaN};
        ElevationProfile actual6 = ElevationProfileComputer.elevationProfile(new testRoute(500, Tab6, 100), 100);
        ElevationProfile expected6 = new ElevationProfile(500, new float[]{2.f, 2.f, 3.f, 4.f, 5.f, 5.f});
        for (int i = 1; i <= 100; i++) {
            assertEquals(expected6.elevationAt(actual6.length() / i), actual6.elevationAt(actual6.length() / i));
        }
    }

    @Test
    void elevationProfileOnKnownValues7() {
       float[] Tab7 = {Float.NaN,2.f,Float.NaN,Float.NaN,5.f,Float.NaN};
       ElevationProfile actual7 = ElevationProfileComputer.elevationProfile(new testRoute(500,Tab7,100),100);
       ElevationProfile expected7 = new ElevationProfile(500, new float[]{2.f,2.f,3.f,4.f,5.f,5.f});
       for(int i = 1; i <= 100; i++){
            assertEquals(expected7.elevationAt(actual7.length()/i),actual7.elevationAt(actual7.length()/i));}
    }

    @Test
    void elevationProfileOnKnownValues8() {
        float[] Tab8 = {Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN};
        ElevationProfile actual8 = ElevationProfileComputer.elevationProfile(new testRoute(500,Tab8,100),100);
        ElevationProfile expected8 = new ElevationProfile(500, new float[]{0.f,0.f,0.f,0.f,0.f,0.f});
        for(int i = 1; i <= 100; i++){
            assertEquals(expected8.elevationAt(actual8.length()/i),actual8.elevationAt(actual8.length()/i));}
    }

    @Test
    void elevationProfileOnKnownValues9() {
        float[] Tab9 = {Float.NaN,Float.NaN,Float.NaN,Float.NaN,5.f,Float.NaN};
        ElevationProfile actual9 = ElevationProfileComputer.elevationProfile(new testRoute(500,Tab9,100),100);
        ElevationProfile expected9 = new ElevationProfile(500, new float[]{5.f,5.f,5.f,5.f,5.f,5.f});
        for(int i = 1; i <= 100; i++){
            assertEquals(expected9.elevationAt(actual9.length()/i),actual9.elevationAt(actual9.length()/i));}
    }


    }

    class testRoute implements Route{

    private int length;
    private float[] samples;
    private double maxLength;

    testRoute(int length, float[] samples, double maxLength){
        this.length = length;
        this.samples = samples.clone();
        this.maxLength = maxLength;
    }
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return length;
    }

    @Override
    public List<Edge> edges() {
        return null;
    }

    @Override
    public List<PointCh> points() {
        return null;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        int numberOfSamples = (int)(Math.ceil(length/maxLength)) + 1;
        double StepLength = length/(numberOfSamples - 1);
        return samples[(int) (position/StepLength)];
    }
}
