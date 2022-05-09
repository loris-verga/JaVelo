package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;

import java.util.function.Consumer;

/**
 * La classe AnnotatedMapManager gère l'affichage de la carte annotée,
 * c'est-à-dire le fond de carte au-dessus duquel sont superposé l'itinéraire
 * et les points de passages.
 */
public final class AnnotatedMapManager {

    /*
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager; */


    /**
     * L'unique constructeur de la classe prend en argument :
     * @param graph le graph du réseau routier.
     * @param tileManager le gestionnaire de tuiles OpenStreetMap
     * @param routeBean le bean de l'itinéraire.
     * @param errorConsumer un consommateur d'erreurs, permettant
     *                      de signaler une erreur.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                               Consumer<String> errorConsumer){



    }
}
