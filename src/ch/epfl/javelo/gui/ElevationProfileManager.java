package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * La classe ElevationProfileManager gère l'affichage et l'interaction avec le profil
 * en long d'un itinéraire.
 */
public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPositionProperty;

    /**
     * Le constructeur de la classe prend en argument :
     * @param elevationProfile Une propriété accessible en lecture seule, contenant
     *                         le profil à afficher. La propriété contient null dans
     *                         le cas où aucun profil n'est à afficher.
     * @param highlightedPosition Une propriété accessible en lecture seule, contenant
     *                            la position le long du profil à mettre en évidence ;
     *                            elle est de type ReadOnlyDoubleProperty et contient
     *                            NaN dans le cas où aucune position n'est à mettre en
     *                            évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition){
        this.elevationProfileProperty = elevationProfile;
        this.highlightedPositionProperty = highlightedPosition;

    }
}
