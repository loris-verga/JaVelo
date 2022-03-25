package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * La classe SingleRoute publique et immuable (donc finale), représente un itinéraire simple,
 * c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire. Elle implémente l'interface Route.
 *
 * @author Loris Verga (345661)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private final double[] positionArray;

    /**
     * L'unique constructeur de la classe Single retourne l'itinéraire simple composé des arêtes données
     * ou lève IllegalArgumentException si la liste d'arêtes est vide.
     *
     * @param edges liste d'arrêtes
     */
    public SingleRoute(List<Edge> edges) {

        Preconditions.checkArgument(!(edges.isEmpty()));
        this.edges = edges;

        //Pas possible d'utiliser un itérateur ici
        positionArray = new double[edges.size() + 1];
        positionArray[0] = 0.0;
        double length = 0.0;
        for (int i = 0; i < positionArray.length - 1; ++i) {
            int index = i + 1;
            length = length + edges.get(i).length();
            positionArray[index] = length;
        }
    }


    /**
     * Cette méthode retourne l'index du segment de l'itinéraire contenant la position donnée
     * qui vaut toujours 0 dans le cas d'un itinéraire simple.
     *
     * @param position donnée en mètre
     * @return toujours 0 dans le cas d'un itinéraire simple.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * Cette méthode retourne la longueur de l'itinéraire en mètres.
     *
     * @return
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
     * Cette méthode retourne la totalité des arêtes de l'itinéraire.
     *
     * @return une liste de Edge
     */
    @Override
    public List<Edge> edges() {
        List<Edge> liste = new ArrayList<>();
        liste.addAll(edges);
        return liste;
    }

    /**
     * Cette méthode retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     *
     * @return une liste de PointCh
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList();
        points.add(edges.get(0).fromPoint());
        for (Edge g : edges) {
            points.add(g.toPoint());
        }
        return points;
    }

    /**
     * Cette méthode retourne le point se trouvant à la position donnée le long de l'itinéraire.
     *
     * @param position position du point
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = modifiedPosition(position);
        int resultBinarySearch = binarySearch(position);
        if (resultBinarySearch >= 0) {
            return this.points().get(resultBinarySearch);
        } else {
            int indexLessThanTheValue = Math.abs(resultBinarySearch)-2;
            double positionOnTheEdge = pos - positionArray[indexLessThanTheValue];
            return edges.get(indexLessThanTheValue).pointAt(positionOnTheEdge);
        }
    }

    /**
     * Cette méthode retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     *
     * @param position
     * @return
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
     * Cette méthode retourne l'altitude à la position donnée le long de l'itinéraire
     * qui peut valoir NaN si l'arête contenant cette position n'a pas de profil.
     *
     * @param position position du point
     * @return
     */
    @Override
    public double elevationAt(double position) {
        double pos = this.modifiedPosition(position);
        int resultBinarySearch = this.binarySearch(position);
        if (resultBinarySearch>=0){
            if (resultBinarySearch==0){
                return edges.get(resultBinarySearch).elevationAt(0);}
            return edges.get(resultBinarySearch-1).elevationAt(edges.get(resultBinarySearch-1).length());
        }
        int indexOfEdge = resultBinarySearch*(-1)-2;
        ListIterator iterator = edges.listIterator();
        double distanceOnEdge = pos;
        while (iterator.nextIndex() < indexOfEdge){
            Edge edge = (Edge)iterator.next();
            distanceOnEdge = distanceOnEdge - edge.length();
        }
        return edges.get(indexOfEdge).elevationAt(distanceOnEdge);
    }

    /**
     * Cette méthode retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     *
     * @param point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        PointCh pointClosest;
        int indexOfEdge= 0;

        double positionOfPointClosestTo = edges.get(0).positionClosestTo(point);
        if (positionOfPointClosestTo<0){
            pointClosest = edges.get(0).fromPoint();
        }
        else if (positionOfPointClosestTo>edges.get(0).length()){
            pointClosest = edges.get(0).toPoint();
        }
        else {
            pointClosest = edges.get(0).pointAt(positionOfPointClosestTo);
        }

        double squaredDistanceToReference = pointClosest.squaredDistanceTo(point);

        int index = 0;


        for (Edge oneEdge : edges){


            double position = oneEdge.positionClosestTo(point);
            PointCh pointCandidate;
            if (position <0){
                pointCandidate = oneEdge.fromPoint();
            }
            if (position> oneEdge.length()){
                pointCandidate = oneEdge.toPoint();
            }
            else{
                pointCandidate = oneEdge.pointAt(position);}

            double squaredDistCandidate = pointCandidate.squaredDistanceTo(point);

            if (squaredDistCandidate<squaredDistanceToReference){
                indexOfEdge = index;
                positionOfPointClosestTo = position;
                pointClosest = pointCandidate;
                squaredDistanceToReference = squaredDistCandidate;
            }
            index++;
        }
        ListIterator iterator = edges.listIterator();
        while (iterator.nextIndex()<indexOfEdge){
            positionOfPointClosestTo  = (double)positionOfPointClosestTo + ((Edge)(iterator.next())).length();
        }

        return new RoutePoint(pointClosest, positionOfPointClosestTo, Math.sqrt(squaredDistanceToReference));
    }


    /**
     * Cette méthode effectue une binarySearch avec le tableau de position.
     * @param position
     * @return
     */
    private int binarySearch(double position){
        return Arrays.binarySearch(positionArray, modifiedPosition(position));
    }

    /**
     * Cette méthode
     * @param position
     * @return
     */
    private double modifiedPosition(double position){
        if (position < 0) {
            return 0.0;
        }
        if (position > this.length()) {
            return length();
        }
        return position;
    }

}
