package ch.epfl.javelo.routing;

/**
 * L'interface CostFunction présente une fonction de coût. Elle n'est dotée que d'une méthode abstraite.
 *
 * @author Loris Verga (345661)
 */
public interface CostFunction {

    /**
     * Cette méthode retourne le facteur par lequel la longueur de l'arête d'identité edgeId,
     * partant du nœud d'identité nodeId, doit être multipliée ;
     * ce facteur doit impérativement être supérieur ou égal à 1.
     *Le facteur de coût peut être infini (Double.POSITIVE_INFINITY)
     * ce qui exprime le fait que l'arête ne peut absolument pas être empruntée.
     * Cela équivaut à considérer que l'arête n'existe pas.
     * @param nodeId
     * @param edgeId  L'argument edgeId passé à costFactor est l'identité de l'arête,
     *               et pas son index dans la liste des arêtes sortant du nœud d'identité edgeId.
     *               En d'autres termes, il s'agit d'un entier compris entre 0 et le nombre d'arêtes dans le graphe.
     *               Bien évidemment, l'arête portant cette identité doit être l'une des arêtes sortant du nœud d'identité nodeId.
     * @return le facteur, un double
     */
    double costFactor(int nodeId, int edgeId);
}
