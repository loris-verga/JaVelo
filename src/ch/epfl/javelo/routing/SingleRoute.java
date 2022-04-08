package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * La classe SingleRoute publique et immuable, représente un itinéraire simple,
 * c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire.
 *
 * @author Loris Verga (345661)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;
    //Tableau qui contient la position le long de l'itinéraire des nœuds se situant entre les arrêtes.
    private final double[] positionArray;

    /**
     * L'unique constructeur de la classe Single retourne l'itinéraire simple composé des arêtes données
     * ou lève IllegalArgumentException si la liste d'arêtes est vide.
     *
     * @param edges liste d'arrêtes
     */
    public SingleRoute(List<Edge> edges) {

        Preconditions.checkArgument(!(edges.isEmpty()));
        this.edges = new ArrayList<>(edges);

        //Initialisation du tableau positionArray :
        positionArray = new double[edges.size() + 1];
        positionArray[0] = 0.0;
        double length = 0.0;
        ListIterator<Edge> iterator = edges.listIterator();
        while (iterator.nextIndex()<positionArray.length-1){
            length += iterator.next().length();
            positionArray[iterator.nextIndex()] = length;
        }
    }


    /**
     * La méthode indexOfSegmentAt retourne l'index du segment de l'itinéraire contenant la position donnée
     * qui vaut toujours zéro dans le cas d'un itinéraire simple.
     *
     * @param position donnée en mètre
     * @return toujours zéro dans le cas d'un itinéraire simple.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * La méthode length retourne la longueur de l'itinéraire en mètres.
     *
     * @return la longueur de l'itinéraire (double).
     */
    @Override
    public double length() {
        double length = 0;
        for (Edge g : edges) {
            length = length + g.length();
        }
        return length;
    }

    /**
     * La méthode edges méthode retourne la totalité des arêtes de l'itinéraire.
     *
     * @return une liste de Edge.
     */
    @Override
    public List<Edge> edges() {
        return new ArrayList<>(edges);
    }

    /**
     * La méthode points retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     *
     * @return une liste de PointCh
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        points.add(edges.get(0).fromPoint());
        for (Edge g : edges) {
            points.add(g.toPoint());
        }
        return points;
    }

    /**
     * La méthode pointAt retourne le point se trouvant à la position donnée le long de l'itinéraire.
     *
     * @param position La position du point.
     * @return un PointCh se trouvant sur l'itinéraire.
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = modifiedPosition(position);
        int resultBinarySearch = binarySearch(position);
        if (resultBinarySearch >= 0) {
            return this.points().get(resultBinarySearch);
        } else {
            int indexLessThanTheValue = Math.abs(resultBinarySearch) - 2;
            double positionOnTheEdge = pos - positionArray[indexLessThanTheValue];
            return edges.get(indexLessThanTheValue).pointAt(positionOnTheEdge);
        }
    }

    /**
     * La méthode nodeClosestTo retourne l'identité du nœud appartenant à l'itinéraire
     * et se trouvant le plus proche de la position donnée.
     *
     * @param position La position sur l'itinéraire.
     * @return l'identité du nœud.
     */
    @Override
    public int nodeClosestTo(double position) {
        double pos = modifiedPosition(position);
        int resultBinarySearch = binarySearch(position);
        if (resultBinarySearch >= 0) {
            if (resultBinarySearch == 0) {
                return edges.get(0).fromNodeId();
            }
            return edges.get(resultBinarySearch - 1).toNodeId();
        } else {
            int indexLessThanTheValue = Math.abs(resultBinarySearch) - 2;
            int indexBiggerThanTheValue = Math.abs(resultBinarySearch) - 1;
            double positionOnTheEdge = pos - positionArray[indexLessThanTheValue];
            double differenceWithNextNode = positionArray[indexBiggerThanTheValue] - pos;
            if (positionOnTheEdge <= differenceWithNextNode) {
                return edges.get(indexLessThanTheValue).fromNodeId();
            }
            return edges.get(indexLessThanTheValue).toNodeId();
        }
    }


    /**
     * La méthode elevationAt retourne l'altitude à la position donnée le long de l'itinéraire
     * qui peut valoir NaN si l'arête contenant cette position n'a pas de profil.
     *
     * @param position La position du point.
     * @return l'élévation (un double)
     */
    @Override
    public double elevationAt(double position) {
        double pos = this.modifiedPosition(position);
        int resultBinarySearch = this.binarySearch(position);
        if (resultBinarySearch >= 0) {
            if (resultBinarySearch == 0) {
                return edges.get(resultBinarySearch).elevationAt(0);
            }
            return edges.get(resultBinarySearch - 1).elevationAt(edges.get(resultBinarySearch - 1).length());
        }
        int indexOfEdge = resultBinarySearch * (-1) - 2;
        ListIterator<Edge> iterator = edges.listIterator();
        double distanceOnEdge = pos;
        while (iterator.nextIndex() < indexOfEdge) {
            Edge edge = iterator.next();
            distanceOnEdge = distanceOnEdge - edge.length();
        }
        return edges.get(indexOfEdge).elevationAt(distanceOnEdge);
    }

    /**
     * La méthode pointClosestTo retourne le point de l'itinéraire se trouvant
     * le plus proche du point de référence donné.
     *
     * @param point le PointCh de référence
     * @return le RoutePoint se trouvant sur l'itinéraire
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        //Nous obtenons le premier point de l'itinéraire et le comparons avec tous les autres points
        //et nous gardons le meilleur.
        PointCh pointClosest;
        double positionPointClosest = edges.get(0).positionClosestTo(point);
        double squaredDistPointClosest;

        if (positionPointClosest<0){
            positionPointClosest = 0;
            pointClosest = edges.get(0).fromPoint();
        }
        else if (positionPointClosest > edges.get(0).length()){
            positionPointClosest = edges.get(0).length();
            pointClosest = edges.get(0).toPoint();
        }
        else {
            positionPointClosest = edges.get(0).positionClosestTo(point);
            pointClosest = edges.get(0).pointAt(positionPointClosest);
        }

        squaredDistPointClosest = pointClosest.squaredDistanceTo(point);

        int indexOfEdge = 0;
        int indexCounter = 0;
        PointCh pointCandidate;
        double positionCandidate;
        double squaredDistCandidate;
        for (Edge oneEdge : edges){
            positionCandidate = oneEdge.positionClosestTo(point);
            if (positionCandidate < 0){
                positionCandidate = 0;
                pointCandidate = oneEdge.fromPoint();
            }
            else if (positionCandidate > oneEdge.length()){
                positionCandidate = oneEdge.length();
                pointCandidate = oneEdge.toPoint();
            }
            else {
                pointCandidate = oneEdge.pointAt(positionCandidate);
            }
            squaredDistCandidate = pointCandidate.squaredDistanceTo(point);


            if (squaredDistCandidate < squaredDistPointClosest){
                indexOfEdge = indexCounter;
                pointClosest = pointCandidate;
                positionPointClosest = positionCandidate;
                squaredDistPointClosest = squaredDistCandidate;
            }

            indexCounter++;
        }
        ListIterator<Edge> iterator = edges.listIterator();
        while (iterator.nextIndex() < indexOfEdge){
            positionPointClosest += iterator.next().length();
        }
        return new RoutePoint(pointClosest, positionPointClosest, Math.sqrt(squaredDistPointClosest));
    }


    /**
     * La méthode binarySearch effectue une binarySearch avec le tableau de position.
     *
     * @param position position le long de l'itinéraire
     * @return le résultat de la binary search
     */
    private int binarySearch(double position) {
        return Arrays.binarySearch(positionArray, modifiedPosition(position));
    }

    /**
     * La méthode modifiedPosition modifie la position pour qu'elle soit valide
     * pour l'utilisation de nos méthodes spécifiques.
     *
     * @param position position le long de l'itinéraire
     * @return la position modifiée
     */
    private double modifiedPosition(double position) {
        if (position < 0) {
            return 0.0;
        }
        if (position > this.length()) {
            return length();
        }
        return position;
    }

}
