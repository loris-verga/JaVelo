package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * L'interface Route représente un itinéraire. Elle sera implémentée par deux classes,
 * l'une représentant un itinéraire simple entre deux points de passage
 * et l'autre représentant un itinéraire multiple passant par au moins un point de passage intermédiaire.
 *
 * @author Loris Verga (345661)
 */
public interface Route {

    /**
     * Cette méthode retourne l'index du segment à la position donnée (en mètres)
     *
     * @param position donnée en mètre
     * @return index du segment
     */
    int indexOfSegmentAt(double position);


    /**
     * Cette méthode retourne la longueur de l'itinéraire, en mètre
     * @return la longueur de l'itinéraire
     */
    double length();

    /**
     * Cette méthode retourne la totalité des arrêtes de l'itinéraire.
     * @return une liste de Edge
     */
    List<Edge> edges();

    /**
     * Cette méthode retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire,
     * @return une liste de PointCh
     */
    List<PointCh> points();


    /**
     * Cette méthode retourne le point se trouvant à la position donnée le long de l'itinéraire.
     * @param position position du point
     * @return le PointCh se trouvant à cette position.
     */
    PointCh pointAt(double position);


    /**
     * Cette méthode retourne l'identité appartenant à l'itinéraire et se trouvant le plus
     * proche de la position donnée
     * @param position
     * @return le noeud le plus proche de la position donnée se trouvant sur l'itinéraire
     */
    int nodeClosestTo(double position);

    /**
     * Cette méthode retourne le point de l'itinéraire se trouvant le plus proche du point
     * de référence donnée
     * @param point
     * @return
     */
    RoutePoint pointClosestTo(PointCh point);

}


