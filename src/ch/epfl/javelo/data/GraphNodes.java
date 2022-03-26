package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Cette Classe représente le tableau de tous les nœuds du graphe JaVelo.
 *
 * @author Juan B Iaconucci (342153)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Methode qui retourne le nombre total de nœud.
     *
     * @return le nombre total de nœuds
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * Methode qui retourne la coordonnée Est du nœud.
     *
     * @param nodeId identité du nœud
     * @return la coordonnée Est du nœud
     */
    public double nodeE(int nodeId) {
        int intE = buffer.get((nodeId) * NODE_INTS + OFFSET_E);
        return Q28_4.asDouble(intE);
    }

    /**
     * Methode qui donne la coordonnée Nord du nœud
     *
     * @param nodeId identité du nœud
     * @return la coordonnée Nord du nœud
     */
    public double nodeN(int nodeId) {
        int intN = buffer.get((nodeId) * NODE_INTS + OFFSET_N);
        return Q28_4.asDouble(intN);
    }

    /**
     * Methode qui donne le nombre d'arêtes sortant du nœud
     *
     * @param nodeId identité du nœud
     * @return le nombre d'arêtes sortant du nœud
     */
    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     * Methode qui retourne l'identité de la edgeIndex-ième arête sortant du nœud.
     *
     * @param nodeId identité du nœud
     * @return l'identité de la edgeIndex-ième arête sortant du nœud
     */
    public int edgeId(int nodeId, int edgeIndex) {
        Preconditions.checkArgument(0 <= edgeIndex && edgeIndex < outDegree(nodeId));

        int firstEdgeId = Bits.extractUnsigned(buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES), 0, 28);

        return firstEdgeId + edgeIndex;
    }
}
