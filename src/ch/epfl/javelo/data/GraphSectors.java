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
    private static final int NUMBER_OF_SECTORS = 128;

    /**
     * La classe Sector permet de représenter un secteur JaVelo.
     */
    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Cette méthode retourne la liste de tous les secteurs ayant une intersection
     * avec le carré centré au point donné et de côté égal au double de la distance donnée.
     *
     * @param center   Les coordonnées du centre du carré.
     * @param distance La moitié du côté du carré.
     * @return une liste contenant tous les sectors qui sont inclus dans le carré de cotée le double de distance.
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        List<Sector> listOfSectorsInArea = new ArrayList<>();

        //On calcule les dimensions d'un secteur.
        double sectorLengthE = SwissBounds.WIDTH / NUMBER_OF_SECTORS;
        double sectorLengthN = SwissBounds.HEIGHT / NUMBER_OF_SECTORS;

        //On calcule les nouvelles coordonnées du centre du point, sur le repère orthonormé baser sur les secteurs.
        double newCenterE = (center.e() - SwissBounds.MIN_E) / sectorLengthE;
        double newCenterN = (center.n() - SwissBounds.MIN_N) / sectorLengthN;

        //On re-dimensionne les dimensions du carré dans ce nouveau repère.
        double newSideLengthE = distance / sectorLengthE;
        double newSideLengthN = distance / sectorLengthN;

        //On calcule les points d'extrémité du rectangle englobant tous les secteurs dans le "carré".
        int infX = (int) Math.floor(newCenterE - newSideLengthE);
        int infY = (int) Math.floor(newCenterN - newSideLengthN);
        int maxX = (int) Math.ceil(newCenterE + newSideLengthE);
        int maxY = (int) Math.ceil(newCenterN + newSideLengthN);

        //On vérifie que ces points sont bien à l'intérieur du repère.
        infX = Math2.clamp(0, infX, 128);
        infY = Math2.clamp(0, infY, 128);
        maxX = Math2.clamp(0, maxX, 128);
        maxY = Math2.clamp(0, maxY, 128);

        for (int x = infX; x < maxX; x++) {
            for (int y = infY; y < maxY; y++) {
                //On calcule l'identité du secteur.
                int sectorId = x + y * NUMBER_OF_SECTORS;
                //On trouve l'identité du premier nœud à l'intérieur de buffer.
                int startNodeId = buffer.getInt(
                        sectorId * SECTOR_INTS + OFFSET_ID_OF_FIRST_NODE);
                //On trouve le nombre total de nœud à l'intérieur de buffer.
                int nBOfNodes = Short.toUnsignedInt(
                        buffer.getShort(
                                sectorId * SECTOR_INTS + OFFSET_NB_OF_NODE));
                //On calcule l'identité du dernier nœud.
                int endNodeId = startNodeId + nBOfNodes;
                //On ajoute le nouveau secteur dans la liste de secteur
                listOfSectorsInArea.add(new Sector(startNodeId, endNodeId));
            }
        }
        return listOfSectorsInArea;
    }
}
