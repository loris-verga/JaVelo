package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * L'interface Route représente un itinéraire. Elle est implémentée par deux classes,
 * l'une représentant un itinéraire simple entre deux points de passage
 * et l'autre représentant un itinéraire multiple passant par au moins un point de passage intermédiaire.
 *
 * @author Loris Verga (345661)
 */
public interface Route {

    /**
     * La méthode indexOfSegmentAt retourne l'index du segment à la position donnée (en mètres).
     *
     * @param position donnée en mètre.
     * @return l'index du segment.
     */
    int indexOfSegmentAt(double position);


    /**
     * La méthode length retourne la longueur de l'itinéraire, en mètre
     * @return la longueur de l'itinéraire.
     */
    double length();

    /**
     * La méthode edges retourne la totalité des arrêtes de l'itinéraire.
     * @return une liste de Edge
     */
    List<Edge> edges();

    /**
     * La méthode points retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire,
     * @return une liste de PointCh
     */
    List<PointCh> points();


    /**
     * La méthode pointAt retourne le point se trouvant à la position donnée le long de l'itinéraire.
     * @param position position du point
     * @return le PointCh se trouvant à cette position.
     */
    PointCh pointAt(double position);


    /**
     * La méthode nodeClosestTo retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus
     * proche de la position donnée.
     * @param position position le long de l'itinéraire-
     * @return le nœud le plus proche de la position donnée se trouvant sur l'itinéraire
     */
    int nodeClosestTo(double position);

    /**
     * La méthode pointClosestTo retourne le point de l'itinéraire se trouvant le plus proche du point
     * de référence donnée
     * @param point le point dont on cherche le point le plus proche sur l'itinéraire.
     * @return le point le plus proche sur l'itinéraire.
     */
    RoutePoint pointClosestTo(PointCh point);


    /**
     * La méthode elevationAt retourne l'altitude à la position donnée le long de l'itinéraire.
     * @param position position le long de l'itinéraire.
     * @return l'élévation (double).
     */
    double elevationAt(double position);






}


