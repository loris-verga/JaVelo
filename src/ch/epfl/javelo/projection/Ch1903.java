package ch.epfl.javelo.projection;

/**
 *
 * La classe Ch1903, publique, finale et non instantiable contient des méthodes statiques permettant
 * de faire des conversions entre les coordonnées WGS 84 et les coordonnées suisses.
 *
 * Attention les méthodes de CH1903 ne valident pas leurs arguments, la validité des coordonnées
 * sera vérifiée par les classes représentant les points.
 *
 * @author Loris Verga (345661)
 */
public final class Ch1903 {

    //Constructeur privé de sorte que la classe ne soit pas instantiable.
    private Ch1903(){}

    /**
     * Cette méthode retourne la coordonnée E (est) dans le système suisse
     * du point de longitude lon et latitude lat dans le système WGS84
     *
     * @param lon longitude dans le système WGS84
     * @param lat latitude dans le système WGS84
     * @return double e, coordonnée E (est) du point dans le système suisse.
     */
    public static double e(double lon, double lat) {
        double longitude = Math.toDegrees(lon);
        double latitude = Math.toDegrees(lat);
        double lambda_un = 0.0001 * (3600 * longitude - 26782.5);
        double phi_un = 0.0001 * (3600 * latitude - 169028.66);
        return 2600072.37
                + 211455.93 * lambda_un
                - 10938.51 * lambda_un * phi_un
                - 0.36 * lambda_un * Math.pow(phi_un, 2)
                - 44.54 * Math.pow(lambda_un, 3);
    }

    /**
     * La méthode n retourne la coordonnée N (nord) dans le système suisse
     * du point de longitude lon et latitude lat dans le système WGS84.
     *
     * @param lon La longitude dans le système WGS84
     * @param lat La latitude dans le système WGS84
     * @return double e, coordonnée N (nord) du point dans le système suisse.
     */
    public static double n(double lon, double lat) {
        double longitude = Math.toDegrees(lon);
        double latitude = Math.toDegrees(lat);

        double lambda_un = 0.0001 * (3600 * longitude - 26782.5);
        double phi_un = 0.0001 * (3600 * latitude - 169028.66);
        return 1200147.07
                + 308807.95 * phi_un
                + 3745.25 * lambda_un * lambda_un
                + 76.63 * phi_un * phi_un
                - 194.56 * lambda_un * lambda_un * phi_un
                + 119.79 * phi_un * phi_un * phi_un;
    }


    /**
     * La méthode lon retourne la longitude dans le système WGS84.
     *
     * @param e La coordonnée E (est) dans le système suisse.
     * @param n La coordonnée N (nord) dans le système suisse.
     * @return la coordonnée longitude dans le système WGS84.
     */
    public static double lon(double e, double n) {
        double x = 0.000001 * (e - 2600000);
        double y = 0.000001 * (n - 1200000);

        double lambda_zero = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * y * y
                - 0.0436 * x * x * x;

        double lambda = lambda_zero * 100 / 36;
        return Math.toRadians(lambda);
    }


    /**
     * La méthode lat retourne la latitude dans le système WGS84.
     *
     * @param e La coordonnée E (est) dans le système suisse.
     * @param n La coordonnée N (nord) dans le système suisse.
     * @return la coordonnée latitude dans le système WGS84.
     */
    public static double lat(double e, double n) {
        double x = 0.000001 * (e - 2600000);
        double y = 0.000001 * (n - 1200000);

        double phi_zero = 16.9023892
                + 3.238272 * y
                - 0.270978 * x * x
                - 0.002528 * y * y
                - 0.0447 * x * x * y
                - 0.0140 * y * y * y;

        double phi = phi_zero * 100 / 36;
        return Math.toRadians(phi);
    }
}
