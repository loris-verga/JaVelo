package ch.epfl.test;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4TestOur {

    private static final double DELTA = 1e-4;

        @Test
        void ofIntOnKnownValues(){
            var actual1 = Q28_4.ofInt(0b0);
            var expected1 = 0b0;
            assertEquals(expected1,actual1);

            var actual2 = Q28_4.ofInt(0b101001);
            var expected2 = 0b1010010000;
            assertEquals(expected2,actual2);

            var actual3 = Q28_4.ofInt(0b0101010100101110101);
            var expected3 = 0b01010101001011101010000;
            assertEquals(expected3,actual3);

            var actual4 = Q28_4.ofInt(0b11111111111111111111);
            var expected4 = 0b111111111111111111110000;
            assertEquals(expected4,actual4);

            var actual5 = Q28_4.ofInt(0b1000_0000_0000_0000_0000_0000_0001);
            var expected5 = 0b1000_0000_0000_0000_0000_0000_0001_0000;
            assertEquals(expected5,actual5);
        }

        @Test
        void asDoubleOnKnownValues(){
            var actual1 = Q28_4.asDouble(0b0);
            var expected1 = 0.0;
            assertEquals(expected1,actual1);

            var actual2 = Q28_4.asDouble(0b101001);
            var expected2 = 2.5625;
            assertEquals(expected2,actual2);

            var actual3 = Q28_4.asDouble(0b10100101110101);
            var expected3 = 663.3125 ;
            assertEquals(expected3,actual3);

            var actual4 = Q28_4.asDouble(0b1111_1111_1111_1111_1111_1111_1111_1111);
            var expected4 = -0.0625;
            assertEquals(expected4,actual4);

            var actual5 = Q28_4.asDouble(0b1000_0000_0000_0000_0000_0000_0000_0001);
            var expected5 = -134217727.9375;
            assertEquals(expected5,actual5);
        }

        @Test
        void asFloatOnKnownValues(){
            var actual1 = Q28_4.asFloat(0b0);
            var expected1 = 0.0f;
            assertEquals(expected1,actual1);

            var actual2 = Q28_4.asFloat(0b101001);
            var expected2 = 2.5625f;
            assertEquals(expected2,actual2);

            var actual3 = Q28_4.asFloat(0b10100101110101);
            var expected3 = 663.3125f ;
            assertEquals(expected3,actual3);

            var actual4 = Q28_4.asFloat(0b1111_1111_1111_1111_1111_1111_1111_1111);
            var expected4 = -0.0625f;
            assertEquals(expected4,actual4);

            var actual5 = Q28_4.asFloat(0b1000_0000_0000_0000_0000_0000_0000_0001);
            var expected5 = -134217727.9375f;
            assertEquals(expected5,actual5);
        }

}