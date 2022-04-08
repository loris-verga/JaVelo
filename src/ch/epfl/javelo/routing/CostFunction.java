package ch.epfl.javelo.routing;

/**
 * L'interface CostFunction présente une fonction de coût. Elle n'est dotée que d'une méthode abstraite.
 *
 * @author Loris Verga (345661)
 */
public interface CostFunction {

    /**
     * La méthode costFactor retourne le facteur par lequel la longueur de l'arête d'identité edgeId,
     * partant du nœud d'identité nodeId, doit être multipliée.
     *
     * @param nodeId l'id du noeud
     * @param edgeId L'identité de l'arête.
     * @return le facteur (double).
     */
    double costFactor(int nodeId, int edgeId);
}
