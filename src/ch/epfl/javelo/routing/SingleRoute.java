package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * La classe SingleRoute publique et immuable (donc finale), représente un itinéraire simple,
 * c.-à-d. reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire. Elle implémente l'interface Route.
 *
 * @author Loris Verga (345661)
 */
public final class SingleRoute implements Route {

    private List<Edge> edges;
    private double[] positionArray; //TODO CHECK IF IT IS SORTED!


    /**
     * L'unique constructeur de la classe Single retourne l'itinéraire simple composé des arêtes données
     * ou lève IllegalArgumentException si la liste d'arêtes est vide.
     *
     * @param edges liste d'arrêtes
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!(edges.isEmpty()));
        this.edges = edges;


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
        for (int i = 0; i < edges.size(); ++i) {
            length = length + edges.get(i).length();
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
        for (int i = 0; i < edges.size(); ++i) {
            liste.add(edges.get(i));
        }
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
        for (int i = 0; i < edges.size(); ++i) {
            points.add(edges.get(i).toPoint());
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
        double pos;
        if (position < 0) {
            pos = 0;
        } else if (position > this.length()) {
            pos = length();
        } else {
            pos = position;
        }


        int result = Arrays.binarySearch(positionArray, pos);
        if (result >= 0) {
            return this.points().get(result);
        } else {
            int indexLessThanTheValue = Math.abs(result) - 2;
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
        double pos;
        if (position < 0) {
            pos = 0;
        } else if (position > this.length()) {
            pos = length();
        } else {
            pos = position;
        }

        int result = Arrays.binarySearch(positionArray, pos);
        if (result >= 0) {
            if (result == 0) {
                return edges.get(0).fromNodeId();
            }
            return edges.get(result - 1).toNodeId();
        } else {
            int indexLessThanTheValue = Math.abs(result) - 2;
            int indexBiggerThanTheValue = Math.abs(result) - 1;
            double positionOnTheEdge = pos - positionArray[indexLessThanTheValue];
            double differenceWithNextNode = positionArray[indexBiggerThanTheValue] - pos;
            if (positionOnTheEdge <= differenceWithNextNode) {
                return edges.get(result).toNodeId();
            } else {
                return edges.get(result - 1).toNodeId();
            }
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
        double distance = position;
        int i = 0;
        while (position > 0){
            position = position - edges.get(i).length();
            i = i+1;
        }
        int index = i-1;
        position = Math.abs(position);
        return edges.get(index).elevationAt(position);
    }

    /**
     * Cette méthode retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     *
     * @param point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double positionOfPointClosestTo = edges.get(0).positionClosestTo(point);
        PointCh pointClosest = edges.get(0).pointAt(positionOfPointClosestTo);
        double squaredDistanceToReference = pointClosest.squaredDistanceTo(point);
        for (int i = 1; i<edges.size(); ++i){

            Edge oneEdge = edges.get(i);
            double position = oneEdge.positionClosestTo(point);
            PointCh pointCandidate = oneEdge.pointAt(position);
            double squaredDistCandidate = pointCandidate.squaredDistanceTo(point);

            if (squaredDistCandidate<squaredDistanceToReference){
                positionOfPointClosestTo = position;
                pointClosest = pointCandidate;
                squaredDistanceToReference = squaredDistCandidate;

            }
        }
        return new RoutePoint(pointClosest, positionOfPointClosestTo, Math.sqrt(squaredDistanceToReference));
    }

}
