package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * GraphSectors représente le tableau contenant les 16384 secteurs de JaVelo.
 *
 * @author Juan B Iaconucci (342153)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_ID_OF_FIRST_NODE = 0;
    private static final int OFFSET_NB_OF_NODE = OFFSET_ID_OF_FIRST_NODE + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_NB_OF_NODE + Short.BYTES;

    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Cette méthode retourne la liste de tous les secteurs ayant une intersection
     * avec le carré centré au point donné et de côté égal au double de la distance donnée.
     *
     * @param center   les coordonnées du centre du carré
     * @param distance la moitié du côté du carré
     * @return une liste contenant tous les sectors qui sont inclus dans le carré de cotée le double de distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        List<Sector> listOfSectorsInArea = new ArrayList<>();

        double sectorLengthE = SwissBounds.WIDTH / 128.0;
        double sectorLengthN = SwissBounds.HEIGHT / 128.0;

        double newCenterE = (center.e() - SwissBounds.MIN_E) / sectorLengthE;
        double newCenterN = (center.n() - SwissBounds.MIN_N) / sectorLengthN;

        double newSideLengthE = distance / sectorLengthE;
        double newSideLengthN = distance / sectorLengthN;

        int infX = (int) Math.floor(newCenterE - newSideLengthE);
        int infY = (int) Math.floor(newCenterN - newSideLengthN);
        int maxX = (int) Math.ceil(newCenterE + newSideLengthE);
        int maxY = (int) Math.ceil(newCenterN + newSideLengthN);

        infX = Math2.clamp(0, infX, 128);
        infY = Math2.clamp(0, infY, 128);
        maxX = Math2.clamp(0, maxX, 128);
        maxY = Math2.clamp(0, maxY, 128);

        for (int x = infX; x < maxX; x++) {
            for (int y = infY; y < maxY; y++) {
                int sectorId = x + y * 128;
                int startNodeId = buffer.getInt(sectorId * SECTOR_INTS + OFFSET_ID_OF_FIRST_NODE);
                int nBOfNodes = Short.toUnsignedInt(buffer.getShort(sectorId * SECTOR_INTS + OFFSET_NB_OF_NODE));
                int endNodeId = startNodeId + nBOfNodes;
                listOfSectorsInArea.add(new Sector(startNodeId, endNodeId));
            }
        }
        return listOfSectorsInArea;
    }
}
