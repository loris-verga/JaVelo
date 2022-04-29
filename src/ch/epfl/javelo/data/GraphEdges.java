package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;

/**
 * L'enregistrement GraphEdge représente le tableau de toutes les arêtes du graphe JaVelo.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_ID_OF_DESTINATION_NODE = 0;
    private static final int OFFSET_LENGTH_OF_EDGE = OFFSET_ID_OF_DESTINATION_NODE + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH_OF_EDGE + Short.BYTES;
    private static final int OFFSET_ID_OF_SET_OF_OSM = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final int EDGE_INTS = OFFSET_ID_OF_SET_OF_OSM + Short.BYTES;

    private static final int PROFIL_TYPE_LENGTH = 2;
    private static final  int SIZE_OF_COMPRESSED_VALUES_WITH_PROFIL_TYPE_2 = 8;
    private static final  int SIZE_OF_COMPRESSED_VALUES_WITH_PROFIL_TYPE_3 = 4;

    /**
     * La méthode isInverted renvoie true si l'arrête passée en argument va dans le sens inverse de la voie OSM.
     *
     * @param edgeId L'identité de l'arrête.
     * @return true ou false selon le sens de l'arrête.
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE) < 0;
    }

    /**
     * La méthode targetNodeId renvoie l'identité du nœud de destination qui se trouve sur l'arrête d'identité donnée.
     *
     * @param edgeId L'identité de l'arrête donnée.
     * @return renvoie l'identité du nœud de destination.
     */
    public int targetNodeId(int edgeId) {

        int destinationNodeId = edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE);

        return (isInverted(edgeId)) ? ~destinationNodeId : destinationNodeId;
    }

    /**
     * La méthode length renvoie la longueur de l'arrête d'identité donnée.
     *
     * @param edgeId L'identité de l'arrête donnée
     * @return La longueur de l'arrête.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(
                Short.toUnsignedInt(
                    edgesBuffer.getShort(
                            EDGE_INTS * edgeId + OFFSET_LENGTH_OF_EDGE)));
    }

    /**
     * La méthode elevationGain retourne la difference de hauteur positive de l'arrête d'identité donnée.
     *
     * @param edgeId L'identité de l'arrête donnée.
     * @return La difference de hauteur positive.
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(
                Short.toUnsignedInt(
                        edgesBuffer.getShort(
                                EDGE_INTS * edgeId + OFFSET_ELEVATION_GAIN)));
    }

    /**
     * La méthode hasProfile renvoie true si l'arrête d'identité donnée à un profil.
     *
     * @param edgeId L'identité de l'arrête donnée.
     * @return true si l'arrête d'identité donnée à un profil.
     */
    public boolean hasProfile(int edgeId) {

        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(
                profileIdAndType, Integer.SIZE - PROFIL_TYPE_LENGTH, PROFIL_TYPE_LENGTH);

        return profilType != 0;
    }

    /**
     * La méthode profilSamples retourne une liste d'échantillons du profil de l'arrête d'identité donnée.
     *
     * @param edgeId L'identité de l'arrête.
     * @return une liste des échantillons du profil de l'arrête d'identité donnée.
     */
    public float[] profileSamples(int edgeId) {

        if (!hasProfile(edgeId)) {
            return new float[]{};
        }

        int lengthOfProfilList = 1 + (int) Math.ceil(length(edgeId) / 2.0);
        float[] profilList = new float[lengthOfProfilList];
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(
                profileIdAndType, Integer.SIZE - PROFIL_TYPE_LENGTH, PROFIL_TYPE_LENGTH);

        int firstSampleId = Bits.extractUnsigned(
                profileIdAndType, 0, Integer.SIZE - PROFIL_TYPE_LENGTH);

        float firstSample = Q28_4.asFloat(
                                Short.toUnsignedInt(
                                    elevations.get(firstSampleId)));

        switch (profilType) {
            case 1: {
                // Si profilType vaut 1 alors les échantillons ne sont pas compressés
                // donc on peut directement les extraire depuis le buffer elevations.

                float newSample;
                for (int i = 0; i < lengthOfProfilList; i++) {
                    newSample = Q28_4.asFloat(
                            Short.toUnsignedInt(
                                    elevations.get(firstSampleId + i)));
                    profilList[i] = newSample;
                }
                break;
            }
            case 2: {
                // Si profilType vaut 2 alors
                // les différences entre chaque échantillon sont stockées par 4 sur 32 bits,
                // donc on utilise la méthode sampleListForCompressedValues qui permet de
                // renvoyer la liste d'échantillons.
                profilList = sampleListForCompressedValues(lengthOfProfilList, firstSample, firstSampleId,
                        SIZE_OF_COMPRESSED_VALUES_WITH_PROFIL_TYPE_2);
                break;
            }
            case 3: {
                // Si profilType vaut 3 alors
                // la differences entre chaque échantillon est stocké par 8 sur 32 bits,
                // donc on utilise la méthode sampleListForCompressedValues qui permet de
                // renvoyer la liste d'échantillons.
                profilList = sampleListForCompressedValues(lengthOfProfilList, firstSample, firstSampleId,
                        SIZE_OF_COMPRESSED_VALUES_WITH_PROFIL_TYPE_3);
                break;
            }
        }

        //Si l'arête est dans le sens inverse alors on inverse le sens du tableau.
        if (isInverted(edgeId)) {
            profilList = invertList(profilList);
        }

        return profilList;
    }

    /**
     * La méthode sampleListForCompressedValues permet de renvoyer la liste d'échantillon de l'arrête d'identité donnée
     * pour les arrêtes qui ont des profils avec des valeurs compressées.
     *
     * @param length                 La longueur de la liste contenant tous les échantillons.
     * @param firstSample            Le premier échantillon.
     * @param firstSampleId          L'index du premier échantillon.
     * @param sizeOfCompressedValues La taille en bit des valeurs compresser.
     * @return la liste d'échantillons de l'arrête d'identité donnée.
     */
    private float[] sampleListForCompressedValues(int length, float firstSample
            ,int firstSampleId, int sizeOfCompressedValues) {

        float[] profilList = new float[length];
        float newSample;
        float difference;
        int encodedSample;
        int startOfBits;

        //On ajoute à la liste de profil le premier échantillon qui n'est pas compressé.
        profilList[0] = firstSample;
        //Le premier échantillon devient l'ancien.
        float previousSample = firstSample;
        //On calcule le nombre de valeurs compresser sur 32 bits.
        int nbOfCompressedValuesInBits = Short.SIZE / sizeOfCompressedValues;

        int indexOfProfilList = 1;
        //On calcule jusqu'à quel index on doit aller sur le buffer elevations.
        int totalElevationIndex = (int) Math.ceil(
                (profilList.length - 1.0) / nbOfCompressedValuesInBits);

        for (int elevationIndex = 1; elevationIndex <= totalElevationIndex; elevationIndex++) {

            //On récupère le set de 32 bits qui contient les différences encodées
            encodedSample = elevations.get(firstSampleId + elevationIndex);

            //Pour chaque différence encoder à l'intérieur d'encodedSample,
            for (int indexOfBits = nbOfCompressedValuesInBits - 1; indexOfBits >= 0
                    && indexOfProfilList < profilList.length; indexOfBits--) {

                //on trouve l'index du premier bit de la difference encodée,
                startOfBits = indexOfBits * sizeOfCompressedValues;
                //on décode la difference pour la mettre en mètre,
                difference = Q28_4.asFloat(
                                Bits.extractSigned(
                                        encodedSample, startOfBits, sizeOfCompressedValues));
                //on trouve le nouvel échantillon en ajoutant à l'ancien échantillon la difference entre
                // le nouvel échantillon et l'ancien,
                newSample = previousSample + difference;
                //on ajoute le nouvel échantillon à la liste d'échantillon.
                profilList[indexOfProfilList] = newSample;
                //Le nouvel échantillon devient l'ancien.
                previousSample = newSample;
                indexOfProfilList++;
            }
        }

        return profilList;
    }

    /**
     * La méthode invertList inverse les éléments d'une liste donnée.
     *
     * @param profilSample La liste que l'on veut inverser.
     * @return une liste inversée.
     */
    private float[] invertList(float[] profilSample) {

        int length = profilSample.length;

        for (int i = 0; i < length/2; i++) {
            float var = profilSample[i];
            profilSample[i] = profilSample[length - (i + 1)];
            profilSample[length - (i + 1)] = var;
        }

        return profilSample;
    }

    /**
     * La méthode attributesIndex renvoie l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     *
     * @param edgeId L'identité de l'arrête donnée.
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     */
    public int attributesIndex(int edgeId) {

        return Short.toUnsignedInt(
                edgesBuffer.getShort(
                        EDGE_INTS * edgeId + OFFSET_ID_OF_SET_OF_OSM));
    }
}
