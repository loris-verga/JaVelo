package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * La classe MultiRoute publique et immuable, représente un itinéraire multiple,
 * c'est-à-dire que l'itinéraire est composé d'une séquence d'itinéraires contigus nommés segment.
 *
 * @author Loris Verga (345661)
 */
public final class MultiRoute implements Route{

    private final List<Route> segments;

    /**
     * Unique constructeur de la classe MultiRoute.
     * @param segments une liste de Routes qui vont constituer cette route multiple.
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!(segments.size() == 0));
        this.segments = new ArrayList<>(segments);
    }


    /**
     * Cette méthode retourne l'index du segment à la position donnée (en mètres)
     *
     * @param position donnée en mètre
     * @return index du segment
     */
    public int indexOfSegmentAt(double position) {
        if (position < 0) {
            return 0;
        }
        double pos = position;
        int counter = 0;

        for (Route route : segments){
            pos -= route.length();
            if (pos < 0){
                return route.indexOfSegmentAt(route.length()+pos) + counter;
            }
            counter += route.indexOfSegmentAt(route.length())+1;
        }
        return counter-1;
    }

    /**
     * Cette méthode retourne la longueur de l'itinéraire, en mètre
     * @return la longueur de l'itinéraire
     */
    public double length(){
        double length = 0;
        for (Route route : segments){
            length += route.length();
        }
        return length;
    }

    /**
     * Cette méthode retourne la totalité des arrêtes de l'itinéraire.
     * @return une liste de Edge
     */
    public List<Edge> edges() {
        List<Edge> listOfEdges = new ArrayList<>();
        for (Route route : segments){
            listOfEdges.addAll(route.edges());
        }
        return listOfEdges;
    }

    /**
     * Cette méthode retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire,
     * @return une liste de PointCh
     */
    public List<PointCh> points(){

        List<PointCh> listOfPoints = new ArrayList<>(segments.get(0).points());

        //On fait en sorte de ne pas mettre le premier point, car il est déjà présent dans le segment précédent.
        ListIterator<Route> iterator = segments.listIterator(1);
        while (iterator.hasNext()) {
            ListIterator<PointCh> iteratorOfOneSegment = iterator.next().points().listIterator(1);
            while (iteratorOfOneSegment.hasNext()){
                listOfPoints.add(iteratorOfOneSegment.next());
            }
        }
        return listOfPoints;
    }


    /**
     * Cette méthode retourne le point se trouvant à la position donnée le long de l'itinéraire.
     * @param position position du point
     * @return le PointCh se trouvant à cette position.
     */
    public PointCh pointAt(double position){
        int indexOfSegment = this.indexOfSubRouteAt(position);
        double pos = this.positionOnTheSegment(position, indexOfSegment);
        return segments.get(indexOfSegment).pointAt(pos);
    }


    /**
     * Cette méthode retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus
     * proche de la position donnée
     * @param position la position le long de l'itinéraire complet.
     * @return le nœud le plus proche de la position donnée se trouvant sur l'itinéraire
     */
    public int nodeClosestTo(double position){
        int indexOfSegment = this.indexOfSubRouteAt(position);
        double pos = positionOnTheSegment(position, indexOfSegment);
        return segments.get(indexOfSegment).nodeClosestTo(pos);
    }

    /**
     * Cette méthode retourne le point de l'itinéraire se trouvant le plus proche du point
     * de référence donnée
     * @param point le pointCh de référence.
     * @return le RoutePoint le plus proche du point de référence
     */
    public RoutePoint pointClosestTo(PointCh point){
        RoutePoint pointClosestTo = segments.get(0).pointClosestTo(point);
        double distanceToRefPointClosest = pointClosestTo.distanceToReference();

        int indexOfSegment = 0;
        ListIterator<Route> iterator = segments.listIterator(1);
        while (iterator.hasNext()){
            RoutePoint pointCandidate = iterator.next().pointClosestTo(point);
            double distanceToRefCandidate = pointCandidate.distanceToReference();
            if (distanceToRefCandidate<distanceToRefPointClosest){
                indexOfSegment++;
                pointClosestTo = pointCandidate;
                distanceToRefPointClosest = distanceToRefCandidate;
            }
        }
        double positionToShift = 0;
        for (int i = 0; i<indexOfSegment; ++i){
            positionToShift += segments.get(i).length();

        }
        return pointClosestTo.withPositionShiftedBy(positionToShift);
    }


    /**
     * Cette méthode retourne l'altitude à la position donnée le long de l'itinéraire.
     * @param position position le long de l'itinéraire entier.
     * @return l'élévation à cette position.
     */
    public double elevationAt(double position){
        int indexOfSegment = this.indexOfSubRouteAt(position);
        double pos = this.positionOnTheSegment(position, indexOfSegment);
        return segments.get(indexOfSegment).elevationAt(pos);
    }


    /**
     * Cette méthode privée retourne la position relative sur un segment.
     * @param position position sur l'ensemble de l'itinéraire
     * @param indexOfSegment index du segment
     * @return position sur le segment
     */
    private double positionOnTheSegment(double position, int indexOfSegment){
        double pos = position;
        ListIterator<Route> iterator = segments.listIterator();
        while (iterator.nextIndex()<indexOfSegment){
            pos -= iterator.next().length();
        }
        return pos;
    }

    /**
     * Cette méthode privée retourne l'index du sous-segment à la position donnée (en mètres)
     *
     * @param position donnée en mètre
     * @return index du segment
     */
    private int indexOfSubRouteAt(double position){
        if (position<0){
            return 0;
        }
        if (position>=this.length()){
            return segments.size()-1;
        }

        double pos = position;
        int index = 0;
        while (pos >= 0) {
            pos = pos - segments.get(index).length();
            index ++;
        }
        return index-1;
    }
}
