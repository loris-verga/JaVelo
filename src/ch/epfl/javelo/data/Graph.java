package ch.epfl.javelo.data;


import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Classe Graph: Cette classe représente le graphe Javelo. Elle est publique et immuable
 *
 * @author  Loris Verga (345661)
 */
public class Graph {


    /**
     * Constructeur publique de la classe Graph
     * @param nodes Les noeuds du graphe
     * @param sectors Les secteurs du graphe.
     * @param edges Les arrêtes du graphe.
     * @param attributeSets L'ensemble des attributs du graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){


    }


    /**
     * Méthode loadFrom:
     * Retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin d'accès est basePath
     * ou lève IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendu n'existe pas.
     * @param basePath: chemin d'accès au répertoire.
     * @return un Graph Javelo
     * @throws IOException si le fichier attendu n'existe pas
     */
    public static Graph loadFrom(Path basePath) throws IOException{


    }


    /**
     * Méthode nodeCount
     * @return retourne le nombre total de noeuds dans le graph.
     */
    public int nodeCount(){

    }


    /**
     * Méthode nodePoint
     * @param nodeId identité du point
     * @return retourne la position du nœud d'identité donnée
     */
    public PointCh nodePoint(int nodeId){

    }

    /**
     * Méthode nodeOutDegree
     * @param nodeId ID du nœud
     * @return retourne le nombre d'arrêtés sortant du nœud d'identité donnée
     */
    public int nodeOutDegree(int nodeId){

    }

    /**
     * La méthode nodeOutEdgeId retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     * @param nodeId identité du noeud
     * @param edgeIndex index de l'arrête
     * @return l'id du noeud
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){

    }

    /**
     * La méthode nodeClosestTo retourne l'identité du nœud se trouvant le plus proche du point donné
     * à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères
     * @param point PointCh donné
     * @param searchDistance rayon dans lequel on cherche
     * @return l'identité du nœud le plus proche.
     */
    public int nodeClosestTo(PointCh point, double searchDistance){

    }

    /**
     * Cette méthode retourne l'identité du nœud destination de l'arête d'identité donnée,
     * @param edgeId id de l'arrête
     * @return l'id du noeud de destination
     */
    public int edgeTargetNodeId(int edgeId){

    }

    /**
     * La méthode edgeIsInverted retourne vrai si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient.
     * @param edgeId
     * @return
     */
    public boolean edgeIsInverted(int edgeId){

    }

    /**
     * Cette méthode retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     * @param edgeId identité de l'arrête
     * @return l'ensemble des attributs OSM liés à l'arrête
     */
    public AttributeSet edgeAttributes(int edgeId){

    }

    /**
     * Cette méthode retourne la longueur, en mètres, de l'arête d'identité donnée.
     * @param edgeId id de l'arrête.
     * @return longueur en mètre de l'arrête.
     */
    double edgeLength(int edgeId){

    }

    /**
     * Cette méthode retourne le dénivelé positif total de l'arête d'identité donnée,
     * @param edgeId id de l'arrête
     * @return le dénivelé total de l'arrête (double)
     */
    public double edgeElevationGain(int edgeId){

    }

    /**
     * La méthode edgeProfile retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction ;
     * si l'arête ne possède pas de profil, alors cette fonction doit retourner Double.NaN pour n'importe quel argument.
     * @param edgeId
     * @return
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){

    }



}
