package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * La classe MultiRoute publique et immuable, représente un itinéraire multiple,
 * c.-à-d. que l'itinéraire est composé d'une séquence d'itinéraires contigus nommés segment.
 *
 * @author Loris Verga (345661)
 */
public final class MultiRoute implements Route{

    private List<Route> segments;

    /**
     * Unique constructeur de la classe MultiRoute.
     * @param segments
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!(segments.size() == 0));

        for (Route segment : segments){
            this.segments.add(segment);
        }
    }


    /**
     * Cette méthode retourne l'index du segment à la position donnée (en mètres)
     *
     * @param position donnée en mètre
     * @return index du segment
     */
    public int indexOfSegmentAt(double position){

        return 0;
    }


    /**
     * Cette méthode retourne la longueur de l'itinéraire, en mètre
     * @return la longueur de l'itinéraire
     */
    public double length(){

        return 0.0;
    }

    /**
     * Cette méthode retourne la totalité des arrêtes de l'itinéraire.
     * @return une liste de Edge
     */
    public List<Edge> edges() {

        return null;
    }

    /**
     * Cette méthode retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire,
     * @return une liste de PointCh
     */
    public List<PointCh> points(){

        return null;
    }


    /**
     * Cette méthode retourne le point se trouvant à la position donnée le long de l'itinéraire.
     * @param position position du point
     * @return le PointCh se trouvant à cette position.
     */
    public PointCh pointAt(double position){

        return null;
    }


    /**
     * Cette méthode retourne l'identité appartenant à l'itinéraire et se trouvant le plus
     * proche de la position donnée
     * @param position
     * @return le noeud le plus proche de la position donnée se trouvant sur l'itinéraire
     */
    public int nodeClosestTo(double position){

        return 0;
    }

    /**
     * Cette méthode retourne le point de l'itinéraire se trouvant le plus proche du point
     * de référence donnée
     * @param point
     * @return
     */
    public RoutePoint pointClosestTo(PointCh point){

        return null;
    }


    /**
     * Cette méthode retourne l'altitude à la position donnée le long de l'itinéraire.
     * @param position
     * @return l'élévation
     */
    public double elevationAt(double position){

        return 0.0;
    }






}
