package ch.epfl.test;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTestOur {

    @Test
    void constantWorksOnTrivialValue(){
        double a = 0;
        DoubleUnaryOperator function = Functions.constant(a);
        assertEquals(0, function.applyAsDouble(7438));
    }
    @Test
    void constantWorksOnNonTrivialValue(){
        double a = 5;
        DoubleUnaryOperator function = Functions.constant(5);
        assertEquals(5, function.applyAsDouble(75892304));
    }

    @Test
    void sampledWorksOnTrivialValue() {
        float[] tableau = {0, 0, 0, 0, 0};
        double xMax = 5;
        DoubleUnaryOperator function = Functions.sampled(tableau, xMax);
        assertEquals(function.applyAsDouble(4), 0);
    }

    @Test
    void sampledWorksOnTrivialValue2() {
        float[] tableau = {0, 1, 1};
        double xMax = 2;
        DoubleUnaryOperator function = Functions.sampled(tableau, xMax);
        assertEquals(function.applyAsDouble(1.5), 1);
    }
    @Test
    void sampledWorksOnNonTrivialValue1(){
        float[] tableau = {0, 2, 0};
        double xMax = 2;
        DoubleUnaryOperator function = Functions.sampled(tableau, xMax);
        assertEquals(function.applyAsDouble(0.5), 1);
    }

    @Test
    void sampledWorksOnNonTrivialValue3(){
        float [] tableau = {3, 4, 3 , 43 , 423, 423, 43, 43};
        double xMax = 3;
        DoubleUnaryOperator function = Functions.sampled(tableau, xMax);
        assertEquals(43, function.applyAsDouble(985));
    }
    @Test
    void sampledWorksOnNonTrivialValue4(){
        float [] tableau = {0, 1, 2, 3, 4, 5, 6, 7, 8 ,9, 10};
        double xMax = 10;
        DoubleUnaryOperator function = Functions.sampled(tableau, xMax);
        Assertions.assertEquals(Math2.interpolate(8, 9, 0.5), 8.5);
    }


}