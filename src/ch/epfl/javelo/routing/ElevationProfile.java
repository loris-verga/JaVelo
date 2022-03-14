package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * La classe ElevationProfil représente en long d'un itinéraire simple ou multiple
 *
 *  @author Juan Bautista Iaconucci (342153)
 */
public class ElevationProfile {

    double length;
    float[] elevationSamples;

    /**
     * constructeur public de ElevationProfile
     * @param length attribut longueur de l'itinéraire
     * @param elevationSamples tableau contenant les échantillons d'altitude
     */
    public ElevationProfile(double length, float[] elevationSamples){
        //todo check here if the condition of the elevationSample is correct
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    /**
     * methode length renvoie la longueur de l'itinéraire
     * @return la longueur de l'itinéraire
     */
    public double length() {return length;}

    /**
     * methode minElevation retourne l'altitude minimum du profil en mètres
     * @return l'altitude minimum du profil
     */
    public double minElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length ; i++){
            s.accept(elevationSamples[i]);
        }
        return s.getMin();
    }

    /**
     * methode maxElevation retourne l'altitude maximum du profil en mètres
     * @return l'altitude maximum du profil
     */
    public double maxElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length ; i++){
            s.accept(elevationSamples[i]);
        }
        return s.getMax();
    }

    /**
     * methode totalAscent retourne le dénivelé positif total du profil en mètres
     * @return le dénivelé positif total du profil
     */
    public double totalAscent(){
        double totalAscentValue = 0.0;
        for (int i = 1; i < elevationSamples.length ; i++){
            //todo not sure if this is correct for the "somme de toutes les diff pos entre un echantillon et son predecesseur"
            if (elevationSamples[i-1] < elevationSamples[i]) {
                totalAscentValue += elevationSamples[i] - elevationSamples[i-1];
            }
        }
        return totalAscentValue;
    }

    /**
     * methode totalDescent renvoie le dénivelé négatif total du profil en mètres
     * @return le dénivelé négatif total du profil
     */
    public double totalDescent(){
        double totalDescentValue = 0.0;
        for (int i = 1; i < elevationSamples.length ; i++){
            //todo not sure if this is correct for the "somme de toutes les diff negatives entre un echantillon et son predecesseur"
            if (elevationSamples[i-1] > elevationSamples[i]) {
                totalDescentValue += elevationSamples[i-1] - elevationSamples[i];
            }
        }
        return totalDescentValue;
    }

    /**
     * methode elevationAt renvoie l'altitude du profil à la position donnée
     * @param position la position à laquelle on veut connaitre l'altitude
     * @return l'altitude du profil à la position donnée
     */
    public double elevationAt(double position){
        //if(position < 0){return elevationSamples[0];}
        //if(position > length){return elevationSamples[elevationSamples.length - 1];}
        //todo ask Loris about this... have no idea if this is correct...

        DoubleUnaryOperator asSampled = Functions.sampled(elevationSamples, length);
        return asSampled.applyAsDouble(position);
    }

}
