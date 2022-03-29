package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * La classe ElevationProfileComputer représente un calculateur de profil en long.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public final class ElevationProfileComputer {
    private ElevationProfileComputer() {}

    /**
     * La méthode elevationProfile retourne le profil le long de l'itinéraire.
     *
     * @param route         l'itinéraire dont on veut obtenir le profil.
     * @param maxStepLength la distance maximale entre les échantillons du profil
     * @return le profil le long de l'itinéraire
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);

        int numberOfSamples = (int) (Math.ceil(route.length() / maxStepLength)) + 1;
        double StepLength = route.length() / (numberOfSamples - 1);
        float[] elevationSamples = new float[numberOfSamples];

        for (int i = 0; i < numberOfSamples; i++) {
            elevationSamples[i] = (float) (route.elevationAt(i * StepLength));
        }

        ArrayList<Float> elevationSamplesArrayList = new ArrayList<>();

        for (float sample : elevationSamples) {
            elevationSamplesArrayList.add(sample);
        }

        int indexOfFirstNotNan = -1;
        int indexOfLastNotNan = -1;
        int index = 0;

        while (index < numberOfSamples && indexOfFirstNotNan == -1) {
            if (!elevationSamplesArrayList.get(index).isNaN()) {
                indexOfFirstNotNan = index;
            }
            index += 1;
        }

        if (indexOfFirstNotNan == -1) {
            Arrays.fill(elevationSamples, 0.f);
            return new ElevationProfile(route.length(), elevationSamples);
        }

        Arrays.fill(elevationSamples, 0, indexOfFirstNotNan, elevationSamples[indexOfFirstNotNan]);

        index = numberOfSamples - 1;
        while (index >= 0 && indexOfLastNotNan == -1) {
            if (!elevationSamplesArrayList.get(index).isNaN()) {
                indexOfLastNotNan = index;
            }
            index -= 1;
        }

        Arrays.fill(elevationSamples, indexOfLastNotNan, numberOfSamples, elevationSamples[indexOfLastNotNan]);

        index = indexOfFirstNotNan;
        int indexOfLeftOfNaNs = 0;
        int indexOfRightOfNaNs;

        while (index <= indexOfLastNotNan) {
            if (!elevationSamplesArrayList.get(index).isNaN()) {
                indexOfLeftOfNaNs = index;
            }
            else {
                int indexToFindNextNumber = index;
                while (elevationSamplesArrayList.get(indexToFindNextNumber).isNaN()) {
                    indexToFindNextNumber += 1;
                }
                indexOfRightOfNaNs = indexToFindNextNumber;

                double y0 = elevationSamples[indexOfLeftOfNaNs];
                double y1 = elevationSamples[indexOfRightOfNaNs];
                double x = (double) (index - indexOfLeftOfNaNs) / (double) (indexOfRightOfNaNs - indexOfLeftOfNaNs);

                elevationSamples[index] = (float) Math2.interpolate(y0, y1, x);

            }
            index += 1;
        }
        return new ElevationProfile(route.length(), elevationSamples);
    }

}
