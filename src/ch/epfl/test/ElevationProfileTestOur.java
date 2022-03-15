package ch.epfl.test;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTestOur {

    @Test
    void constructorOnNegativeLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile newProfile = new ElevationProfile(-5,new float[]{0,9,9,7,6});
        });
    }

    @Test
    void constructorOnNullLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile newProfile = new ElevationProfile(0,new float[]{0,9,9,7,6});
        });
    }

    @Test
    void constructorOnEmptySamples(){
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile newProfile = new ElevationProfile(5,new float[]{});
        });
    }

    @Test
    void constructorOn1Element(){
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile newProfile = new ElevationProfile(5,new float[]{0});
        });
    }

    @Test
    void minElevationOnKnownValues(){
        ElevationProfile profil1 = new ElevationProfile(2, new float[]{0,1,2,3,4});
        ElevationProfile profil2 = new ElevationProfile(1, new float[]{0,1,0,5,0});
        ElevationProfile profil3 = new ElevationProfile(7, new float[]{0,0});
        ElevationProfile profil4 = new ElevationProfile(9, new float[]{-8,3,-5,9,7});
        ElevationProfile profil5 = new ElevationProfile(5, new float[]{100,6,87,3,9});

        assertEquals(0, profil1.minElevation());
        assertEquals(0, profil2.minElevation());
        assertEquals(0, profil3.minElevation());
        assertEquals(-8, profil4.minElevation());
        assertEquals(3, profil5.minElevation());
    }

    @Test
    void maxElevationOnKnownValues(){
        ElevationProfile profil1 = new ElevationProfile(2, new float[]{0,1,2,3,4});
        ElevationProfile profil2 = new ElevationProfile(1, new float[]{0,1,0,5,0});
        ElevationProfile profil3 = new ElevationProfile(7, new float[]{0,0});
        ElevationProfile profil4 = new ElevationProfile(9, new float[]{-8,3,-5,9,7});
        ElevationProfile profil5 = new ElevationProfile(5, new float[]{100,6,87,3,9});

        assertEquals(4, profil1.maxElevation());
        assertEquals(5, profil2.maxElevation());
        assertEquals(0, profil3.maxElevation());
        assertEquals(9, profil4.maxElevation());
        assertEquals(100, profil5.maxElevation());
    }

    @Test
    void totalAscentOnKnownValues(){
        ElevationProfile profil1 = new ElevationProfile(2, new float[]{0,1,2,3,4});
        ElevationProfile profil2 = new ElevationProfile(1, new float[]{0,1,0,5,0});
        ElevationProfile profil3 = new ElevationProfile(7, new float[]{0,0});
        ElevationProfile profil4 = new ElevationProfile(9, new float[]{-8,3,-5,9,7});
        ElevationProfile profil5 = new ElevationProfile(5, new float[]{100,6,87,3,9});

        assertEquals(4, profil1.totalAscent());
        assertEquals(6, profil2.totalAscent());
        assertEquals(0, profil3.totalAscent());
        assertEquals(25, profil4.totalAscent());
        assertEquals(87, profil5.totalAscent());
    }

    @Test
    void totalDescentOnKnownValues(){
        ElevationProfile profil1 = new ElevationProfile(2, new float[]{0,1,2,3,4});
        ElevationProfile profil2 = new ElevationProfile(1, new float[]{0,1,0,5,0});
        ElevationProfile profil3 = new ElevationProfile(7, new float[]{0,0});
        ElevationProfile profil4 = new ElevationProfile(9, new float[]{-8,3,-5,9,7});
        ElevationProfile profil5 = new ElevationProfile(5, new float[]{100,6,87,3,9});

        assertEquals(0, profil1.totalDescent());
        assertEquals(6, profil2.totalDescent());
        assertEquals(0, profil3.totalDescent());
        assertEquals(10, profil4.totalDescent());
        assertEquals(178, profil5.totalDescent());
    }
    @Test
    void elevationAtOnKnownValues(){
        ElevationProfile profil1 = new ElevationProfile(4, new float[]{0,1,2,3,4});
        ElevationProfile profil2 = new ElevationProfile(16, new float[]{0,1,2,3,4});
        ElevationProfile profil3 = new ElevationProfile(7, new float[]{1,1});
        ElevationProfile profil4 = new ElevationProfile(8, new float[]{90,7,76,16,4});
        ElevationProfile profil5 = new ElevationProfile(83, new float[]{10,56});

        assertEquals(0, profil1.elevationAt(-0.05));
        assertEquals(4, profil1.elevationAt(5));

        assertEquals(3.5, profil1.elevationAt(3.5));
        assertEquals(1.5, profil2.elevationAt(6));
        assertEquals(1, profil3.elevationAt(4));
        assertEquals(10, profil4.elevationAt(7));
        assertEquals(26.626506024096386, profil5.elevationAt(30));
    }

}
