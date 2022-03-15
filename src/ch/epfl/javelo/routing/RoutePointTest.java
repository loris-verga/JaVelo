package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {

    @Test
    void routePoint1(){
        RoutePoint routePoint = new RoutePoint(new PointCh(2538639.375, 1153346.8125), 20, 30);

        assertEquals(null, RoutePoint.NONE.point());

        double x = -2.432;
        RoutePoint routePoint2 = routePoint.withPositionShiftedBy(x);
        assertEquals(x, routePoint2.position()-routePoint.position(), 0.000001);

        RoutePoint routePoint3 = new RoutePoint(new PointCh(2538639, 1153346), 20, 60);
        RoutePoint routePoint4 = new RoutePoint(new PointCh(2538639, 1153346), 20, 10);

        assertEquals(routePoint, routePoint3.min(routePoint));
        assertEquals(routePoint, routePoint.min(routePoint3));
        assertEquals(routePoint, routePoint.min(new PointCh(2538639, 1153346), 20, 60));
        assertEquals(routePoint4, routePoint.min(new PointCh(2538639, 1153346), 20, 10));

    }




}