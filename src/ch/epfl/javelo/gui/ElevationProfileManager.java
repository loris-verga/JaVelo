package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * La classe ElevationProfileManager gère l'affichage et l'interaction avec le profil
 * en long d'un itinéraire.
 */
public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPositionProperty;

    private final Pane borderPane;

    private final DoubleProperty mousePositionOnProfileProperty;

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

        this.borderPane = new BorderPane();

        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

    }

    /**
     * La méthode pane retourne le panneau contenant le dessin du profil.
     * @return un Pane JavaFX.
     */
    public Pane pane(){
        return this.borderPane;
    }


    /**
     * La méthode mousePositionOnProfileProperty retourne une propriété en lecture seule
     * contenant la position du pointeur de la souris le long du profil(en mètres, arrondie
     * à l'entier le plus proche), ou NaN si le pointeur de la souris ne se trouve pas
     * au-dessus du profil.
     * @return
     */
    public ReadOnlyDoubleProperty mousePositionProfileProperty(){
        return this.mousePositionOnProfileProperty; //TODO check que la la valeur soit arrondie à l'entier le plus proche.
    }



}
