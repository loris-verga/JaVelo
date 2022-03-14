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

    public ElevationProfile(double length, float[] elevationSamples){
        //todo check here if the condition of the elevationSample is correct
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    public double length() {return length;}

    public double minElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length ; i++){
            s.accept(elevationSamples[i]);
        }
        return s.getMin();
    }

    public double maxElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length ; i++){
            s.accept(elevationSamples[i]);
        }
        return s.getMax();
    }

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

    public double elevationAt(double position){
        //if(position < 0){return elevationSamples[0];}
        //if(position > length){return elevationSamples[elevationSamples.length - 1];}
        //todo was going to do this by myself but then remembered hearing about this so I checked functions out and it has a very very similaire function to this so should I use it..??
        //todo ask Loris about this... have no idea if this is correct...

        DoubleUnaryOperator asSampled = Functions.sampled(elevationSamples, length);
        return asSampled.applyAsDouble(position);
    }

}
