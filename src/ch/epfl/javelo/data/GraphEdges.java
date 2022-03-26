package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * L'enregistrement GraphEdge représente l'ensemble des arrêtes représente le tableau de toutes les arêtes du graphe JaVelo
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_ID_OF_DESTINATION_NODE = 0;
    private static final int OFFSET_LENGTH_OF_EDGE = OFFSET_ID_OF_DESTINATION_NODE + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH_OF_EDGE + Short.BYTES;
    private static final int OFFSET_ID_OF_SET_OF_OSM = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final int EDGE_INTS = OFFSET_ID_OF_SET_OF_OSM + Short.BYTES;

    /**
     * La méthode isInverted renvoie true si l'arrête passée en argument va dans le sens inverse de la voie OSM.
     *
     * @param edgeId l'identité de l'arrête.
     * @return true ou false selon le sens de l'arrête.
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE) < 0;
    }

    /**
     * La méthode targetNodeId renvoie l'identité du nœud de destination qui se trouve sur l'arrête d'identité donnée.
     *
     * @param edgeId d'identité l'arrête donnée
     * @return renvoie l'identité du nœud de destination
     */
    public int targetNodeId(int edgeId) {
        int destinationNodeId = edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE);
        if (!isInverted(edgeId)) {
            return destinationNodeId;
        } else {
            return ~destinationNodeId;
        }
    }

    /**
     * La méthode length renvoie la longueur de l'arrête d'identité donnée.
     *
     * @param edgeId l'identité de l'arrête donnée
     * @return la longueur de l'arrête.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_LENGTH_OF_EDGE)));
    }

    /**
     * La méthode elevationGain retourne la difference de hauteur positive de l'arrête d'identité donnée.
     *
     * @param edgeId l'identité de l'arrête donnée
     * @return la difference de hauteur positive
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_ELEVATION_GAIN)));
    }

    /**
     * hasProfile renvoie true si l'arrête d'identité donnée a un profil
     *
     * @param edgeId l'identité de l'arrête donnée
     * @return true si l'arrête d'identité donnée a un profil
     */
    public boolean hasProfile(int edgeId) {
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(profileIdAndType, 30, 2);
        return profilType != 0;
    }

    /**
     * La méthode profilSamples retourne une liste d'échantillons du profil de l'arrête d'identité donnée.
     *
     * @param edgeId l'identité de l'arrête.
     * @return une liste des échantillons du profil de l'arrête d'identité donnée
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) {
            return new float[]{};
        }

        float[] profilList = new float[1 + (int) Math.ceil(length(edgeId) / 2.0)];
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(profileIdAndType, 30, 2);

        int firstSampleId = Bits.extractUnsigned(profileIdAndType, 0, 30);

        float firstSample = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleId)));
        profilList[0] = firstSample;

        switch (profilType) {
            case 1: {
                float newSample;
                for (int i = 1; i < profilList.length; i++) {
                    newSample = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleId + i)));
                    profilList[i] = newSample;
                }
                break;
            }
            case 2: {
                profilList = sampleListForCompressedValues(profilList, firstSample, firstSampleId, 8);
                break;
            }
            case 3: {
                profilList = sampleListForCompressedValues(profilList, firstSample, firstSampleId, 4);
                break;
            }
        }
        if (isInverted(edgeId)) {
            profilList = invertList(profilList);
        }
        return profilList;
    }

    /**
     * La méthode sampleListForCompressedValues permet de renvoyer la liste d'échantillons de l'arrête d'identité donnée,
     * pour celles qui ont des profils avec des valeurs compressées.
     *
     * @param profilList             liste contenant tous les échantillons
     * @param firstSample            le premier échantillon
     * @param firstSampleId          l'index du premier échantillon
     * @param sizeOfCompressedValues la taille en bit des valeurs compresser
     * @return la liste d'échantillons de l'arrête d'identité donnée
     */
    private float[] sampleListForCompressedValues(float[] profilList, float firstSample, int firstSampleId, int sizeOfCompressedValues) {

        float newSample;
        float difference;
        int encodedSample;

        float previousSample = firstSample;
        int nbOfCompressedValuesInBits = Short.SIZE / sizeOfCompressedValues;

        int indexOfProfilList = 1;
        int totalElevationIndex = (int) Math.ceil((profilList.length - 1.0) / nbOfCompressedValuesInBits);
        for (int elevationIndex = 1; elevationIndex <= totalElevationIndex; elevationIndex++) {
            encodedSample = elevations.get(firstSampleId + elevationIndex);

            for (int indexOfBits = nbOfCompressedValuesInBits - 1; indexOfBits >= 0 && indexOfProfilList < profilList.length; indexOfBits--) {
                int startOfBits = indexOfBits * sizeOfCompressedValues;
                difference = Q28_4.asFloat(Bits.extractSigned(encodedSample, startOfBits, sizeOfCompressedValues));
                newSample = previousSample + difference;
                profilList[indexOfProfilList] = newSample;
                previousSample = newSample;
                indexOfProfilList++;
            }
        }
        return profilList;
    }

    /**
     * La méthode invertList inverse les éléments d'une liste donnée.
     *
     * @param profilSample la liste que l'on veut inverser
     * @return une liste inversée
     */
    private float[] invertList(float[] profilSample) {
        float[] newProfilSample = new float[profilSample.length];
        for (int i = 0; i < profilSample.length; i++) {
            newProfilSample[i] = profilSample[profilSample.length - (i + 1)];
        }
        return newProfilSample;
    }

    /**
     * La méthode attributesIndex renvoie l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée-
     *
     * @param edgeId l'identité de l'arrête donnée
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        //todo first change here
        return Short.toUnsignedInt(edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_ID_OF_SET_OF_OSM));
    }
}
