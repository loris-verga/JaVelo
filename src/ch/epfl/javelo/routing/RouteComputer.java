package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import org.w3c.dom.Node;

import java.util.*;

/** RouteComputer représente un planificateur d'itinéraire
 *
 */
public class RouteComputer {

    private CostFunction costFunction;
    private Graph graph;

    /**
     * Le constructeur de RouteComputer construit un planificateur d'itinéraire pour le graphe et la fonction de coût donnés
     * @param graph le graph sur lequel on veut construire l'itinéraire
     * @param costFunction la fonction cout qui est utilisé pour construire l'itinéraire
     */
    RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * bestRouteBetween retourne l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId dans le graphe passé au constructeur
     * @param startNodeId l'identité du premier noeud sur l'itinéraire
     * @param endNodeId l'identité du dernier noeud sur l'itinéraire
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId dans le graphe passé au constructeur
     */
    public Route bestRouteBetween(int startNodeId ,int endNodeId){
        /** le record WeightedNode
         *
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId !=endNodeId);

        float[] distance = new float[graph.nodeCount()];
        int[] prédécesseur = new int[graph.nodeCount()];

        for(int nodeId = 0; nodeId < graph.nodeCount(); nodeId += 1) {
            distance[nodeId] = Float.POSITIVE_INFINITY;
            prédécesseur[nodeId] = 0;
        }

        distance[startNodeId] = 0.f;
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        en_exploration.add(new WeightedNode(startNodeId, 0));

        while( ! en_exploration.isEmpty()){
            WeightedNode nodeWithMinDistance = en_exploration.remove();
            //todo not sure about this
            distance[nodeWithMinDistance.nodeId()] = Float.NEGATIVE_INFINITY;

            if(nodeWithMinDistance.nodeId() == endNodeId){
                en_exploration.clear();
            }
            else {

                for(int edgeIndex = 0; edgeIndex < graph.nodeOutDegree(nodeWithMinDistance.nodeId()) ; edgeIndex += 1) {
                    int nodeIdOutEdge = graph.nodeOutEdgeId(nodeWithMinDistance.nodeId(), edgeIndex);
                    //todo not sure
                    if (distance[nodeIdOutEdge] != Double.NEGATIVE_INFINITY) {
                        int edgeId = graph.nodeOutEdgeId(nodeIdOutEdge, edgeIndex);
                        float newDistance = (float) (nodeWithMinDistance.distance +
                                                        costFunction.costFactor(nodeWithMinDistance.nodeId, edgeId)
                                                                * graph.edgeLength(edgeId));

                        if (newDistance < distance[nodeIdOutEdge]) {
                            distance[nodeIdOutEdge] = newDistance;
                            prédécesseur[nodeIdOutEdge] = nodeWithMinDistance.nodeId();
                            en_exploration.add(new WeightedNode(nodeIdOutEdge, newDistance));

                        }
                    }
                }
            }
        }

        int toNodeId = endNodeId;
        int fromNodeId = prédécesseur[endNodeId];
        int edgeIndex;
        int edgeId;
        List<Edge> edgesList = new ArrayList<>();
        Stack<Edge> edgesStack = new Stack<>();

        while (toNodeId != startNodeId){
            //todo if constructor then missing profil, if of then missing edge id
            //todo see if this fix problem
            edgeIndex = 0;
            while (edgeIndex < graph.nodeOutDegree(fromNodeId)) {
                edgeId = graph.nodeOutEdgeId(fromNodeId , edgeIndex);
                if(graph.edgeTargetNodeId(edgeId) == toNodeId) {
                    edgesStack.push(Edge.of(graph, edgeId, fromNodeId, toNodeId));
                    toNodeId = fromNodeId;
                    fromNodeId = prédécesseur[toNodeId];
                    edgeIndex = graph.nodeOutDegree(fromNodeId);
                }
            edgeIndex += 1;
            }
        }

        for (Edge edge : edgesStack) {
            edgesList.add(edgesStack.pop());
        }

        return new SingleRoute(edgesList);
    }
}
