package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** La classe ElevationProfileComputer représente un calculateur de profil en long.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    /**
     * La méthode elevationProfile retourne le profil le long de l'itinéraire.
     * @param route l'itinéraire dont on veut obtenir le profil.
     * @param maxStepLength la distance maximale entre les échantillons du profil
     * @return le profil le long de l'itinéraire
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        //todo really need to check this
        Preconditions.checkArgument(maxStepLength > 0);

        int spacingBetweenEachProfil = (int)(Math.ceil(route.length()/maxStepLength));
        ArrayList<Float> profilArrayList = new ArrayList<>();

        for(int i = 0; i * spacingBetweenEachProfil <= route.length()/spacingBetweenEachProfil; i++){
            profilArrayList.add((float)(route.elevationAt(i * spacingBetweenEachProfil)));
        }

        float[] elevationSamples = new float[]{profilArrayList.size()};
        int indexOfFirstNotNan=-1;
        int indexOfLastNotNan=-1;
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

        Arrays.fill(elevationSamples, indexOfLastNotNan , elevationSamples.length - 1, elevationSamples[indexOfLastNotNan]);

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
                double x = (index - indexOfLeftOfNaNs)/(indexOfRightOfNaNs - indexOfLeftOfNaNs);
                elevationSamples[index] = (float) Math2.interpolate(y0,y1,x);

            }
        index += 1;
        }
        return new ElevationProfile(route.length() , elevationSamples );
    }

}
