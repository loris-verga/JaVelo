package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** GraphSectors représente le tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Juan B Iaconucci (342153)
 */
public record GraphSectors(ByteBuffer buffer) {

    //todo not sure about this
    record Sector(int startNodeId, int endNodeId){}

    public List<Sector> sectorsInArea(PointCh center, double distance){
        //todo have no clue how to do this

        return null;
    }
}
