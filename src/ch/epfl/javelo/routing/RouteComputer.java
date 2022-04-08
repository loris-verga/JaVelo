package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * La classe RouteComputer représente un planificateur d'itinéraire.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public class RouteComputer {

    private final CostFunction costFunction;
    private final Graph graph;

    /**
     * Le constructeur de RouteComputer crée un planificateur d'itinéraire pour le graphe et la fonction de coût donnée.
     *
     * @param graph        le graph sur lequel on veut construire l'itinéraire
     * @param costFunction la fonction cout qui est utilisé pour construire l'itinéraire
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

        for (int nodeId = 0; nodeId < graph.nodeCount(); nodeId += 1) {
            distance[nodeId] = Float.POSITIVE_INFINITY;
            predecessor[nodeId] = 0;
        }

        distance[startNodeId] = 0.f;
        PriorityQueue<WeightedNode> nodesInExploration = new PriorityQueue<>();
        nodesInExploration.add(new WeightedNode(startNodeId, 0.f));

        while (!nodesInExploration.isEmpty()) {
            WeightedNode nodeWithMinDistance = nodesInExploration.remove();

            if (nodeWithMinDistance.nodeId == endNodeId) {
                nodesInExploration.clear();

                int toNodeId = endNodeId;
                int fromNodeId = predecessor[endNodeId];
                int edgeIndex;
                int edgeId;

                Stack<Edge> edgesStack = new Stack<>();

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

                while (!(edgesStack.empty())) {
                    edgesList.add(edgesStack.pop());
                }

                return new SingleRoute(edgesList);
            }

            if(distance[nodeWithMinDistance.nodeId] == Float.NEGATIVE_INFINITY){
                continue;
            }

            for (int edgeIndex = 0; edgeIndex < graph.nodeOutDegree(nodeWithMinDistance.nodeId()); edgeIndex += 1) {
                int edgeId = graph.nodeOutEdgeId(nodeWithMinDistance.nodeId, edgeIndex);
                int nodeIdOutEdge = graph.edgeTargetNodeId(edgeId);

                if (distance[nodeIdOutEdge] != Double.NEGATIVE_INFINITY) {
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
            }

            distance[nodeWithMinDistance.nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }
}
