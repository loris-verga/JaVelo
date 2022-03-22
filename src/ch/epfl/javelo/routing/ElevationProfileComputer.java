package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Classe ElevationProfileComputer représente un calculateur de profil en long
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    /**
     * Methode elevationProfile qui retourne le profil le long de l'itinéraire
     * @param route l'itinéraire que l'on veut avoir le profil
     * @param maxStepLength la distance maximale entre les échantillons du profil
     * @return le profil le long de l'itinéraire
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        int numberOfSamples = (int)(Math.ceil(route.length()/maxStepLength)) + 1;
        double StepLength = route.length()/(numberOfSamples - 1);
        ArrayList<Float> profilArrayList = new ArrayList<>();

        for(int i = 0; i < numberOfSamples ; i++){
            profilArrayList.add((float)(route.elevationAt(i * StepLength)));
        }

        float[] elevationSamples = new float[numberOfSamples];
        int indexOfFirstNotNan = -1;
        int indexOfLastNotNan = -1;
        int index = 0;

        for(int i = 0; i < elevationSamples.length; i++){
            elevationSamples[i] = profilArrayList.get(i);
        }

        while (index < profilArrayList.size() && indexOfFirstNotNan == -1){
            if (profilArrayList.get(index).isNaN() == false){
                indexOfFirstNotNan = index;
            }
            index += 1;
        }

        if(indexOfFirstNotNan == -1){
            Arrays.fill(elevationSamples, 0.f);
            return new ElevationProfile(route.length(),elevationSamples);
        }

        Arrays.fill(elevationSamples,0, indexOfFirstNotNan, elevationSamples[indexOfFirstNotNan]);

        index = profilArrayList.size() - 1 ;
        while (index >= 0 && indexOfLastNotNan == -1){
            if (profilArrayList.get(index).isNaN() == false){
                indexOfLastNotNan = index;
            }
            index -= 1;
        }

        Arrays.fill(elevationSamples, indexOfLastNotNan , elevationSamples.length , elevationSamples[indexOfLastNotNan]);

        index = indexOfFirstNotNan;
        int indexOfLeftOfNaNs = 0;
        int indexOfRightOfNaNs = 0;

        while( index <= indexOfLastNotNan ){
            if(profilArrayList.get(index).isNaN() == false){
                indexOfLeftOfNaNs = index;
            }
            else{
                int indexToFindNextNumber = index;
                while(profilArrayList.get(indexToFindNextNumber).isNaN()){
                    indexToFindNextNumber += 1;
                }
                indexOfRightOfNaNs = indexToFindNextNumber;

                double y0 = elevationSamples[indexOfLeftOfNaNs];
                double y1 = elevationSamples[indexOfRightOfNaNs];
                double x = (double)(index - indexOfLeftOfNaNs)/(double)(indexOfRightOfNaNs - indexOfLeftOfNaNs);
                elevationSamples[index] = (float) Math2.interpolate(y0,y1,x);

            }
        index += 1;
        }
        return new ElevationProfile(route.length() , elevationSamples);
    }

}
