package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * La classe ElevationProfil représente le profil en long d'un itinéraire simple ou multiple.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public class ElevationProfile {

    private final double length;

    private final double minElevation;
    private final double maxElevation;
    private final double totalAscent;
    private final double totalDescent;
    private final DoubleUnaryOperator elevationProfilAsSampled;

    /**
     * constructeur public de ElevationProfile
     *
     * @param length           attribut longueur de l'itinéraire
     * @param elevationSamples tableau contenant les échantillons d'altitude
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        double totalAscentValue = 0.0;
        double totalDescentValue = 0.0;
        s.accept(elevationSamples[0]);

        for (int i = 1; i < elevationSamples.length; i++) {
            s.accept(elevationSamples[i]);

            if (elevationSamples[i - 1] < elevationSamples[i]) {
                totalAscentValue += elevationSamples[i] - elevationSamples[i - 1];
            }

            if (elevationSamples[i - 1] > elevationSamples[i]) {
                totalDescentValue += elevationSamples[i - 1] - elevationSamples[i];
            }

        }
        this.minElevation = s.getMin();
        this.maxElevation = s.getMax();

        this.totalAscent = totalAscentValue;
        this.totalDescent = totalDescentValue;

        this.elevationProfilAsSampled = Functions.sampled(elevationSamples, length);
    }

    /**
     * methode length renvoie la longueur de l'itinéraire
     *
     * @return la longueur de l'itinéraire
     */
    public double length() {
        return length;
    }

    /**
     * methode minElevation retourne l'altitude minimum du profil en mètres
     *
     * @return l'altitude minimum du profil
     */
    public double minElevation() {
        return minElevation;
    }

    /**
     * methode maxElevation retourne l'altitude maximum du profil en mètres
     *
     * @return l'altitude maximum du profil
     */
    public double maxElevation() {
        return maxElevation;
    }

    /**
     * methode totalAscent retourne le dénivelé positif total du profil en mètres
     *
     * @return le dénivelé positif total du profil
     */
    public double totalAscent() {
        return totalAscent;
    }

    /**
     * methode totalDescent renvoie le dénivelé négatif total du profil en mètres
     *
     * @return le dénivelé négatif total du profil
     */
    public double totalDescent() {
        return totalDescent;
    }

    /**
     * methode elevationAt renvoie l'altitude du profil à la position donnée
     *
     * @param position la position à laquelle on veut connaitre l'altitude
     * @return l'altitude du profil à la position donnée
     */
    public double elevationAt(double position) {
        return elevationProfilAsSampled.applyAsDouble(position);
    }

}
