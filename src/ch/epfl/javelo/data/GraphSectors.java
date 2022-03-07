package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.*;

/** GraphSectors représente le tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Juan B Iaconucci (342153)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_ID_OF_FIRST_NODE = 0;
    private static final int OFFSET_NB_OF_NODE = OFFSET_ID_OF_FIRST_NODE + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_NB_OF_NODE + Short.BYTES;

    record Sector(int startNodeId, int endNodeId){}

    /**
     * Methode qui retourne une liste contenant tous les sectors qui sont incluent dans le carré de cotée le double de distance
     *
     * @param center les coordonnées du centre du carré
     * @param distance la moitié du coté du carré
     * @return une liste contenant tous les sectors qui sont incluent dans le carré de cotée le double de distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        //todo lots of test on this one

        List<Sector> listOfSectorsInArea = new ArrayList<>();

        double sectorLengthE = SwissBounds.WIDTH / 128.0;
        double sectorLengthN = SwissBounds.HEIGHT / 128.0;

        double newCenterE = (center.e() - SwissBounds.MIN_E) / sectorLengthE;
        double newCenterN = (center.n() - SwissBounds.MIN_N) / sectorLengthN;
        double newSideLengthE = distance / sectorLengthE;
        double newSideLengthN = distance / sectorLengthN;

        int infX = (int) Math.floor(newCenterE - newSideLengthE) ;
        int infY = (int) Math.floor(newCenterN - newSideLengthN);
        int maxX = (int) Math.ceil(newCenterE + newCenterE);
        int maxY = (int) Math.ceil(newCenterE + newCenterN);

        for (int x = infX; x < maxX; x++){
            for (int y = infY; y < maxY; y++) {
                int sectorId = x + y * 128;
                int startNodeId = buffer.getInt(sectorId * SECTOR_INTS + OFFSET_ID_OF_FIRST_NODE);
                int nBOfNodes = Short.toUnsignedInt(buffer.getShort(sectorId * SECTOR_INTS + OFFSET_NB_OF_NODE));
                int endNodeId = startNodeId + nBOfNodes - 1;
                listOfSectorsInArea.add( new Sector(startNodeId, endNodeId));
            }
        }
        return listOfSectorsInArea;
    }
}
