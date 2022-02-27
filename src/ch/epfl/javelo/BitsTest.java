package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Classe BitsTest
 *
 * La classe BitsTest permet de tester la classe Bits
 *
 * @author Juan B Iaconucci (342153)
 */


class BitsTest {

    @Test
    void extractSignedThrowOnNegativeStart(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, -50,9);
        });
    }

    @Test
    void extractSignedThrowOnTooBigStart(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 50,9);
        });
    }

    @Test
    void extractSignedThrowOnNegativeLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 0,-10);
        });
    }

    @Test
    void extractSignedThrowOnTooBigLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 0,50);
        });
    }

    @Test
    void extractSignedWorksOnKnownValues(){

        var actual1 = Bits.extractSigned(0b11001010111111101011101010111110, 7,0);
        var expected1 = 0b0;
        assertEquals(expected1,actual1);

        var actual2 = Bits.extractSigned(0b11001010111111101011101010111110, 8,4);
        var expected2 = 0b11111111111111111111111111111010;
        assertEquals(expected2,actual2);

        var actual3 = Bits.extractSigned(0b11001010111111101011101010111110, 0,4);
        var expected3 = 0b11111111111111111111111111111110;
        assertEquals(expected3,actual3);

        var actual4 = Bits.extractSigned(0b11001010111111101011101010111110, 28,4);
        var expected4 = 0b11111111111111111111111111111100;
        assertEquals(expected4,actual4);

        var actual5 = Bits.extractSigned(0b11001010111111101011101010111110, 0,32);
        var expected5 = 0b11001010111111101011101010111110;
        assertEquals(expected5,actual5);
    }




    @Test
    void extractUnsignedThrowOnNegativeStart(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, -50,9);
        });
    }

    @Test
    void extractUnsignedThrowOnTooBigStart(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 50,9);
        });
    }

    @Test
    void extractUnsignedThrowOnNegativeLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 0,-10);
        });
    }

    @Test
    void extractUnsignedThrowOnTooBigLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 0,50);
        });
    }

    @Test
    void extractUnsignedThrowOn32Size(){
        assertThrows(IllegalArgumentException.class, () -> {
            //todo ask if this is correct
            Bits.extractUnsigned(0b11001010111111101011101010111110, 0,32);
        });
    }

    @Test
    void extractUnsignedWorksOnKnownValues(){

        var actual1 = Bits.extractUnsigned(0b11001010111111101011101010111110, 0,0);
        var expected1 = 0b0;
        assertEquals(expected1,actual1);

        var actual2 = Bits.extractUnsigned(0b11001010111111101011101010111110, 8,4);
        var expected2 = 0b1010;
        assertEquals(expected2,actual2);

        var actual3 = Bits.extractUnsigned(0b11001010111111101011101010111110, 0,4);
        var expected3 = 0b1110;
        assertEquals(expected3,actual3);

        var actual4 = Bits.extractUnsigned(0b11001010111111101011101010111110, 28,4);
        var expected4 = 0b1100;
        assertEquals(expected4,actual4);

        //todo check if this is correct
        var actual5 = Bits.extractUnsigned(0b11001010111111101011101010111110, 0,31);
        var expected5 = 0b01001010111111101011101010111110;
        assertEquals(expected5,actual5);
    }

}