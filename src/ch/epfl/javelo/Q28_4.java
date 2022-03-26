package ch.epfl.javelo;


/**
 * La classe Q28_4 contient des méthodes statiques qui permettent de convertir des nombres entre la représentation Q28.4
 * et d'autres représentations.
 *
 * @author Juan B Iaconucci (342153)
 */
public final class Q28_4 {

    private Q28_4() {
    }

    /**
     * Cette méthode retourne la valeur Q28.4 correspondant à l'entier donné
     *
     * @param i entier à convertir
     * @return valeur convertie en Q28,4
     */
    public static int ofInt(int i) {

        return i << 4;
    }

    /**
     * Cette méthode retourne la valeur de type double égale à la valeur Q28.4 donnée
     *
     * @param q28_4 valeur en format q28_4
     * @return valeur convertie en double
     */
    public static double asDouble(int q28_4) {

        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Cette méthode retourne la valeur de type float correspondant à la valeur Q28.4 donnée.
     *
     * @param q28_4 valeur en format q28_4
     * @return valeur convertie en float
     */
    public static float asFloat(int q28_4) {

        return Math.scalb((float) q28_4, -4);
    }
}
