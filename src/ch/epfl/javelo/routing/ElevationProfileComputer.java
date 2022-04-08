package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
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
     * @param route         L'itinéraire dont on veut obtenir le profil.
     * @param maxStepLength La distance maximale entre les échantillons du profil.
     * @return le profil le long de l'itinéraire.
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        Preconditions.checkArgument(maxStepLength > 0);

        //On calcule le nombre total de d'échantillons.
        int numberOfSamples = (int) (Math.ceil(route.length() / maxStepLength)) + 1;
        //On calcule l'espacement entre chaque capture d'échantillons.
        double stepLength = route.length() / (numberOfSamples - 1);
        //On crée le tableau d'échantillons
        float[] elevationSamples = new float[numberOfSamples];

        //On récupère un échantillon après chaque intervalle stepLength
        // et on le stocke dans le tableau elevationSample.
        for (int i = 0; i < numberOfSamples; i++) {
            elevationSamples[i] = (float) (route.elevationAt(i * stepLength));
        }

        int indexOfFirstNotNan = -1;
        int indexOfLastNotNan = -1;
        int sampleIndex = 0;

        //On cherche l'index du premier échantillon qui n'est pas un NotANumber,
        // dans le tableau elevationSamples.
        while (sampleIndex < numberOfSamples && indexOfFirstNotNan == -1) {

            if (!Float.isNaN(elevationSamples[sampleIndex])) {
                indexOfFirstNotNan = sampleIndex;
            }
            sampleIndex += 1;
        }

        //S'il y a que des NotANumbers dans le tableau elevationSamples,
        //alors on remplace ces valeurs par 0
        //et on crée un objet ElevationProfile à partir de ce nouveau tableau.
        if (indexOfFirstNotNan == -1) {

            Arrays.fill(elevationSamples, 0.f);
            return new ElevationProfile(route.length(), elevationSamples);
        }

        //Sinon on remplace les valeurs NotANumbers se trouvant dans les premières cases du tableau,
        //par la première valeur qui n'est pas un NotANumber.
        Arrays.fill(elevationSamples, 0, indexOfFirstNotNan
                , elevationSamples[indexOfFirstNotNan]);

        sampleIndex = numberOfSamples - 1;

        //On cherche l'index du dernier échantillon qui n'est pas un NotANumber,
        // dans le tableau elevationSamples.
        while (sampleIndex >= 0 && indexOfLastNotNan == -1) {
            if (!Float.isNaN(elevationSamples[sampleIndex])) {
                indexOfLastNotNan = sampleIndex;
            }
            sampleIndex -= 1;
        }

        //On remplace les valeurs NotANumbers se trouvant dans les dernières cases du tableau,
        //par la dernière valeur qui n'est pas un NotANumber.
        Arrays.fill(elevationSamples, indexOfLastNotNan,
                numberOfSamples, elevationSamples[indexOfLastNotNan]);

        sampleIndex = indexOfFirstNotNan;
        int indexOfLeftOfNaNs = 0;
        int indexOfRightOfNaNs;

        //On parcourt les valeurs se trouvant au milieu du tableau
        //se trouvant entre la première et la dernière valeurs qui ne sont pas un NotANumber.
        while (sampleIndex <= indexOfLastNotNan) {

            //Si la valeur n'est pas un NotANumber
            //son index devient l'index de la valeur à gauche des NotANumber
            if (!Float.isNaN(elevationSamples[sampleIndex])){
                indexOfLeftOfNaNs = sampleIndex;
            }
            //Sinon on cherche la prochaine valeur qui n'est pas un NotANumber
            //qui devient l'index de la valeur à droite des NotANumbers
            //et par interpolation utilisant ces valeurs,
            //on trouve les valeurs correspondant aux valeurs NotANumber.
            else {
                int indexToFindNextNumber = sampleIndex;
                while (Float.isNaN(elevationSamples[indexToFindNextNumber])) {
                    indexToFindNextNumber += 1;
                }
                indexOfRightOfNaNs = indexToFindNextNumber;

                double y0 = elevationSamples[indexOfLeftOfNaNs];
                double y1 = elevationSamples[indexOfRightOfNaNs];
                double x = (double) (sampleIndex - indexOfLeftOfNaNs)
                        / (double) (indexOfRightOfNaNs - indexOfLeftOfNaNs);

                elevationSamples[sampleIndex] = (float) Math2.interpolate(y0, y1, x);

            }
            sampleIndex += 1;
        }

        return new ElevationProfile(route.length(), elevationSamples);
    }

}
