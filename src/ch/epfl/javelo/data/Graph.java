package ch.epfl.javelo.data;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;


/**
 * La classe Graph représente le graphe Javelo. Elle est publique et immuable.
 *
 * @author  Loris Verga (345661)
 */
public class Graph {

    //Les attributs de la classe graphe.
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructeur public de la classe Graph
     * @param nodes Les nœuds du graphe
     * @param sectors Les secteurs du graphe.
     * @param edges Les arrêtes du graphe.
     * @param attributeSets L'ensemble des attributs du graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = new ArrayList<>();
        this.attributeSets.addAll(attributeSets);
    }


    /**
     * La méthode loadFrom retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin d'accès est basePath
     * ou lève IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendus n'existe pas.
     * @param basePath: chemin d'accès au répertoire.
     * @return un Graph Javelo
     * @throws IOException si le fichier attendu n'existe pas
     */
    public static Graph loadFrom(Path basePath) throws IOException{

        Path nodesPath = basePath.resolve("nodes.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path attributesPath = basePath.resolve("attributes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");

        IntBuffer nodesBuffer;
        try (FileChannel channelNodes = FileChannel.open(nodesPath)) {
            nodesBuffer = channelNodes.map(FileChannel.MapMode.READ_ONLY, 0, channelNodes.size()).asIntBuffer();
        }

        ByteBuffer edgesBuffer;
        try (FileChannel channelEdges = FileChannel.open(edgesPath)) {
            edgesBuffer = channelEdges.map(FileChannel.MapMode.READ_ONLY, 0, channelEdges.size());
        }

        IntBuffer profile_idsBuffer;
        try(FileChannel channelProfile_ids = FileChannel.open(profile_idsPath)) {
            profile_idsBuffer = channelProfile_ids.map(FileChannel.MapMode.READ_ONLY, 0, channelProfile_ids.size()).asIntBuffer();
        }

        ShortBuffer elevationsBuffer;
        try(FileChannel channelElevations = FileChannel.open(elevationsPath)) {
            elevationsBuffer = channelElevations.map(FileChannel.MapMode.READ_ONLY, 0, channelElevations.size()).asShortBuffer();
        }

        LongBuffer attributeSetsBuffer;
        try(FileChannel channelAttributeSets = FileChannel.open(attributesPath)){
            attributeSetsBuffer = channelAttributeSets.map(FileChannel.MapMode.READ_ONLY, 0, channelAttributeSets.size()).asLongBuffer();
        }

        ArrayList<AttributeSet> listOfAttributeSets = new ArrayList<>();
        for (int i = 0; i<attributeSetsBuffer.capacity(); ++i){
            listOfAttributeSets.add(new AttributeSet(attributeSetsBuffer.get(i)));
        }

        ByteBuffer sectorsBuffer;
        try(FileChannel channelSectors = FileChannel.open(sectorsPath)) {
            sectorsBuffer = channelSectors.map(FileChannel.MapMode.READ_ONLY, 0, channelSectors.size());
        }

        GraphNodes graphNodes = new GraphNodes(nodesBuffer);
        GraphEdges graphEdges = new GraphEdges(edgesBuffer, profile_idsBuffer, elevationsBuffer);
        GraphSectors graphSectors = new GraphSectors(sectorsBuffer);

        return new Graph(graphNodes, graphSectors, graphEdges, listOfAttributeSets);

    }


    /**
     * Méthode nodeCount
     * @return retourne le nombre total de nœuds dans le graph.
     */
    public int nodeCount(){
        return nodes.count();
    }


    /**
     * Méthode nodePoint
     * @param nodeId identité du point
     * @return retourne la position du nœud d'identité donnée
     */
    public PointCh nodePoint(int nodeId){
        double n = nodes.nodeN(nodeId);
        double e = nodes.nodeE(nodeId);
        return new PointCh(e, n);
    }

    /**
     * Méthode nodeOutDegree
     * @param nodeId ID du nœud
     * @return retourne le nombre d'arrêtés sortant du nœud d'identité donnée
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);

    }
    //todo check comment here shouldn't it be "id de l'arete" instead of "l'id du noeud" for the return
    /**
     * La méthode nodeOutEdgeId retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     * @param nodeId identité du nœud
     * @param edgeIndex index de l'arrête.
     * @return l'id du noeud
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * La méthode nodeClosestTo retourne l'identité du nœud se trouvant le plus proche du point donné
     * à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères
     * @param point PointCh donné
     * @param searchDistance rayon dans lequel on cherche
     * @return l'identité du nœud le plus proche.
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        List<GraphSectors.Sector> listOfSectors = sectors.sectorsInArea(point, searchDistance);
        if (listOfSectors.size()==0){
            return -1;
        }


        int indexOfPointClosestTo = listOfSectors.get(0).startNodeId();
        double squaredDistance = point.squaredDistanceTo(nodePoint(indexOfPointClosestTo));

        for (GraphSectors.Sector sector : listOfSectors) {
            int startNodeId = sector.startNodeId();
            int endNodeId = sector.endNodeId();

            for (int a = startNodeId; a <= endNodeId; ++a) {
                PointCh otherPoint = nodePoint(a);
                if (point.squaredDistanceTo(otherPoint) < squaredDistance) {
                    squaredDistance = point.squaredDistanceTo(otherPoint);
                    indexOfPointClosestTo = a;
                }
            }
        }
        if (point.distanceTo(nodePoint(indexOfPointClosestTo))<= searchDistance){
            return indexOfPointClosestTo;}
        else{ return -1;}

    }

    /**
     * Cette méthode retourne l'identité du nœud destination de l'arête d'identité donnée,
     * @param edgeId id de l'arrête
     * @return l'id du nœud de destination
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * La méthode edgeIsInverted retourne vrai si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient.
     * @param edgeId Id de l'arrête.
     * @return true ou false selon le sens de l'arrête.
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);


    }

    /**
     * Cette méthode retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     * @param edgeId identité de l'arrête.
     * @return l'ensemble des attributs OSM liés à l'arrête.
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));

    }

    /**
     * Cette méthode retourne la longueur, en mètres, de l'arête d'identité donnée.
     * @param edgeId id de l'arrête.
     * @return longueur en mètre de l'arrête.
     */
     public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     * Cette méthode retourne le dénivelé positif total de l'arête d'identité donnée,
     * @param edgeId id de l'arrête
     * @return le dénivelé total de l'arrête (double)
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);

    }

    /**
     * La méthode edgeProfile retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction ;
     * si l'arête ne possède pas de profil, alors cette fonction doit retourner Double.NaN pour n'importe quel argument.
     * @param edgeId ID de l'arrête.
     * @return la fonction.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        float [] profileSamples = edges.profileSamples(edgeId);
        if (profileSamples.length == 0){
            return Functions.constant(Double.NaN);
        }
        double xMax = edges.length(edgeId);
        return Functions.sampled(profileSamples, xMax);

    }
}
