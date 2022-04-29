package ch.epfl.test.ourtests.part2;

import ch.epfl.javelo.data.Attribute;
import ch.epfl.javelo.data.AttributeSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTestOur {

    @Test
    void testOnDoubleAttributs(){
        AttributeSet testSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,Attribute.HIGHWAY_SERVICE);
        var actual1 = testSet1.bits();
        var expected1 = 1L;
        assertEquals(expected1, actual1);
    }

    @Test
    void constructorNulAttribute(){
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet testSet = new AttributeSet(-1);
        });
    }

    @Test
    void constructorTooBigAttribute(){
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet testSet = new AttributeSet(0b100000000000000000000000000000000000000000000000000000000000000L);
        });
    }

    @Test
    void constructorLargestPossibleAttribute(){
        AttributeSet testSet = new AttributeSet(0b11111111111111111111111111111111111111111111111111111111111111L);
        var actual1 = testSet.bits();
        var expected1 = 0b11111111111111111111111111111111111111111111111111111111111111L;
        assertEquals(expected1, actual1);
    }

    @Test
    void ofOnKnownValues(){
        AttributeSet testSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        var actual1 = testSet1.bits();
        var expected1 = 1L;
        assertEquals(expected1, actual1);

        AttributeSet testSet2 = AttributeSet.of(Attribute.LCN_YES);
        var actual2 = testSet2.bits();
        var expected2 = 0b10000000000000000000000000000000000000000000000000000000000000L;
        assertEquals(expected2, actual2);

        AttributeSet testSet3 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,
                Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_RESIDENTIAL,
                Attribute.HIGHWAY_FOOTWAY,
                Attribute.HIGHWAY_PATH,
                Attribute.HIGHWAY_UNCLASSIFIED,
                Attribute.HIGHWAY_TERTIARY,
                Attribute.HIGHWAY_SECONDARY,

                Attribute.BICYCLE_YES,
                Attribute.BICYCLE_NO,
                Attribute.BICYCLE_DESIGNATED,
                Attribute.BICYCLE_DISMOUNT,
                Attribute.BICYCLE_USE_SIDEPATH,
                Attribute.BICYCLE_PERMISSIVE,
                Attribute.BICYCLE_PRIVATE,

                Attribute.ICN_YES,
                Attribute.NCN_YES,
                Attribute.RCN_YES,
                Attribute.LCN_YES);
        var actual3 = testSet3.bits();
        var expected3 = 0b11111111111000000000000000000000000000000000000000000011111111L;
        assertEquals(expected3, actual3);

        AttributeSet testSet4 = AttributeSet.of(Attribute.LCN_YES,Attribute.HIGHWAY_SERVICE,Attribute.RCN_YES);
        var actual4 = testSet4.bits();
        var expected4 = 0b11000000000000000000000000000000000000000000000000000000000001L;
        assertEquals(expected4, actual4);
    }

    @Test
    void containsOnKnownValues(){
        AttributeSet testSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        var actual1 = testSet1.contains(Attribute.HIGHWAY_SERVICE);
        var expected1 = true;
        assertEquals(expected1, actual1);

        AttributeSet testSet2 = AttributeSet.of(Attribute.LCN_YES);
        var actual2 = testSet2.contains(Attribute.HIGHWAY_SERVICE);
        var expected2 = false;
        assertEquals(expected2, actual2);

        AttributeSet testSet3 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,
                Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_RESIDENTIAL,
                Attribute.HIGHWAY_FOOTWAY,
                Attribute.HIGHWAY_PATH,
                Attribute.HIGHWAY_UNCLASSIFIED,
                Attribute.HIGHWAY_TERTIARY,
                Attribute.HIGHWAY_SECONDARY,

                Attribute.BICYCLE_YES,
                Attribute.BICYCLE_NO,
                Attribute.BICYCLE_DESIGNATED,
                Attribute.BICYCLE_DISMOUNT,
                Attribute.BICYCLE_USE_SIDEPATH,
                Attribute.BICYCLE_PERMISSIVE,
                Attribute.BICYCLE_PRIVATE,

                Attribute.ICN_YES,
                Attribute.NCN_YES,
                Attribute.RCN_YES,
                Attribute.LCN_YES);
        var actual3 = testSet3.contains(Attribute.BICYCLE_NO);
        var expected3 = true;
        assertEquals(expected3, actual3);

        AttributeSet testSet4 = AttributeSet.of(Attribute.LCN_YES,Attribute.HIGHWAY_SERVICE,Attribute.RCN_YES);
        var actual4 = testSet4.contains(Attribute.HIGHWAY_TRACK);
        var expected4 = false;
        assertEquals(expected4, actual4);
    }

    @Test
    void intersectsOnKnownValues(){
        AttributeSet testSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        AttributeSet testSet2 = AttributeSet.of(Attribute.LCN_YES);
        AttributeSet testSet3 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,
                Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_RESIDENTIAL,
                Attribute.HIGHWAY_FOOTWAY,
                Attribute.HIGHWAY_PATH,
                Attribute.HIGHWAY_UNCLASSIFIED,
                Attribute.HIGHWAY_TERTIARY,
                Attribute.HIGHWAY_SECONDARY,

                Attribute.BICYCLE_YES,
                Attribute.BICYCLE_NO,
                Attribute.BICYCLE_DESIGNATED,
                Attribute.BICYCLE_DISMOUNT,
                Attribute.BICYCLE_USE_SIDEPATH,
                Attribute.BICYCLE_PERMISSIVE,
                Attribute.BICYCLE_PRIVATE,

                Attribute.ICN_YES,
                Attribute.NCN_YES,
                Attribute.RCN_YES,
                Attribute.LCN_YES);
        AttributeSet testSet4 = AttributeSet.of(Attribute.LCN_YES,Attribute.HIGHWAY_SERVICE,Attribute.RCN_YES);

        var actual1 = testSet1.intersects(testSet4);
        var expected1 = true;
        assertEquals(expected1, actual1);


        var actual2 = testSet2.intersects(testSet1);
        var expected2 = false;
        assertEquals(expected2, actual2);

        var actual3 = testSet3.intersects(testSet2);
        var expected3 = true;
        assertEquals(expected3, actual3);

        var actual4 = testSet4.intersects(testSet1);
        var expected4 = true;
        assertEquals(expected4, actual4);

        var actual5 = testSet3.intersects(testSet3);
        var expected5 = true;
        assertEquals(expected5, actual5);
    }

    @Test
    void toStringOnKnownValues(){
        AttributeSet testSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        var actual1 = testSet1.toString();
        var expected1 ="{highway=service}";
        assertEquals(expected1, actual1);

        AttributeSet testSet2 = AttributeSet.of(Attribute.LCN_YES);
        var actual2 = testSet2.toString();
        var expected2 = "{lcn=yes}";
        assertEquals(expected2, actual2);

        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());

        AttributeSet testSet4 = AttributeSet.of(Attribute.LCN_YES,Attribute.HIGHWAY_SERVICE,Attribute.RCN_YES);
        var actual4 = testSet4.toString();
        var expected4 = "{highway=service,rcn=yes,lcn=yes}";
        assertEquals(expected4, actual4);
    }

}