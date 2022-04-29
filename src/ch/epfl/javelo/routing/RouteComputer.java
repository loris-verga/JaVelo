package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

/**
 * La classe RouteComputer représente un planificateur d'itinéraire.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class RouteComputer {

    private static final float DISTANCE_OF_NODE_ALREADY_EXPLORED = Float.NEGATIVE_INFINITY;
    private final CostFunction costFunction;
    private final Graph graph;

    /**
     * Le constructeur de RouteComputer crée un planificateur d'itinéraire pour le graphe et la fonction de coût donnée.
     *
     * @param graph        Le graph sur lequel on veut construire l'itinéraire.
     * @param costFunction la fonction cout qui est utilisé pour construire l'itinéraire.
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * La méthode bestRouteBetween retourne l'itinéraire de coût total minimal allant du nœud d'identité startNodeId
     * au nœud d'identité endNodeI dans le graphe passé au constructeur.
     *
     * @param startNodeId L'identité du premier nœud sur l'itinéraire.
     * @param endNodeId   L'identité du dernier nœud sur l'itinéraire.
     * @return l'itinéraire de coût total minimal.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        /**
         * Le record WeightedNode imbriquer à l'intérieur de bestRouteBetween, permet de contenir
         * l'identité d'un nœud et sa distance.
         * @param nodeId L'identité du nœud
         * @param distance La distance la plus courte entre ce nœud et le nœud de départ.
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {

            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];

        //On initialise les valeurs du tableau distance et du tableau predecessor.
        for (int nodeId = 0; nodeId < graph.nodeCount(); nodeId += 1) {
            distance[nodeId] = Float.POSITIVE_INFINITY;
            predecessor[nodeId] = 0;
        }

        distance[startNodeId] = 0.f;
        PriorityQueue<WeightedNode> nodesInExploration = new PriorityQueue<>();
        nodesInExploration.add(new WeightedNode(startNodeId, 0.f));

        //Pendant que l'on n'a pas fini d'explorer tous les nodes dans la queue.
        while (!nodesInExploration.isEmpty()) {
            //On enlève le nœud avec la plus petite distance du nœud d'arrivée de la queue.
            WeightedNode nodeWithMinDistance = nodesInExploration.remove();

            //Si ce nœud correspond au nœud d'arriver, ça vaut dire qu'on a trouvé l'itinéraire le plus court.
            if (nodeWithMinDistance.nodeId == endNodeId) {
                nodesInExploration.clear();

                int toNodeId = endNodeId;
                int fromNodeId = predecessor[endNodeId];
                int edgeIndex;
                int edgeId;

                Stack<Edge> edgesStack = new Stack<>();

                //En utilisant le tableau predecessor,
                //on arrive à reconstruire l'itinéraire inverse
                //en vérifiant que les identités des arêtes sont bien celles
                //qui contiennent les nodes dans ce tableau
                while (toNodeId != startNodeId) {
                    edgeIndex = 0;

                    while (edgeIndex < graph.nodeOutDegree(fromNodeId)) {
                        edgeId = graph.nodeOutEdgeId(fromNodeId, edgeIndex);

                        if (graph.edgeTargetNodeId(edgeId) == toNodeId) {
                            edgesStack.push(Edge.of(graph, edgeId, fromNodeId, toNodeId));
                            toNodeId = fromNodeId;
                            fromNodeId = predecessor[toNodeId];
                            edgeIndex = graph.nodeOutDegree(fromNodeId);
                        }
                        edgeIndex += 1;
                    }
                }

                List<Edge> edgesList = new ArrayList<>();

                //Comme on a le chemin dans le sens inverse, il faut qu'on l'inverse
                //pour avoir une liste d'arêtes dans le bon sense
                while (!(edgesStack.empty())) {
                    edgesList.add(edgesStack.pop());
                }

                //On crée une SingleRoute à partir du tableau d'arêtes
                return new SingleRoute(edgesList);
            }


            //sinon on vérifie que le nœud n'a pas déjà été parcourus
            //(donc que sa distance soit égal a -infini)
            if(distance[nodeWithMinDistance.nodeId] == DISTANCE_OF_NODE_ALREADY_EXPLORED ){
                continue;
            }

            //On parcourt tous les arêtes sortantes du nœud
            //pour comparer si on a trouvé un chemin plus court pour le nœud d'arriver de l'arête,
            //que celui déjà stocker le tableau distance,
            //si c'est le cas la nouvelle valeur le remplace dans le tableau
            for (int edgeIndex = 0; edgeIndex < graph.nodeOutDegree(nodeWithMinDistance.nodeId()); edgeIndex += 1) {
                int edgeId = graph.nodeOutEdgeId(nodeWithMinDistance.nodeId, edgeIndex);
                int nodeIdOutEdge = graph.edgeTargetNodeId(edgeId);

                float newDistanceToNodeOutEdge = distance[nodeWithMinDistance.nodeId]
                            + (float) (costFunction.costFactor(nodeWithMinDistance.nodeId, edgeId)
                                    * graph.edgeLength(edgeId));

                if (newDistanceToNodeOutEdge < distance[nodeIdOutEdge]) {
                    float eagleEyeDistance = (float) graph.nodePoint(endNodeId)
                            .distanceTo(graph.nodePoint(nodeIdOutEdge));
                    distance[nodeIdOutEdge] = newDistanceToNodeOutEdge;
                    predecessor[nodeIdOutEdge] = nodeWithMinDistance.nodeId;
                    nodesInExploration.add(new WeightedNode(nodeIdOutEdge
                                , newDistanceToNodeOutEdge + eagleEyeDistance));

                    }

            }
            //Le nœud a été explorer donc sa distance devient -infini
            //pour que l'on ne le parcourt pas une 2ème fois.
            distance[nodeWithMinDistance.nodeId] = Float.NEGATIVE_INFINITY;
        }
        //Si on sort de la boucle,
        //ça veux dire qu'un chemin entre les deux nœuds n'existe pas.
        return null;
    }
}
