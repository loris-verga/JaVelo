package ch.epfl.javelo;

/**
 * Classe Math2
 * <p>
 * La classe Math2 offre des méthodes statiques permettant d'effectuer certains calculs mathématiques.
 *
 * @author Juan Bautista Iaconucci (342153)
 */


public final class Math2 {

    private Math2() {
    }

    /**
     * Retourne la partie entière de la division de x par y.
     *
     * @param x La coordonnée x (ne doit pas être negative)
     * @param y La coordonnée y (ne doit pas être negative et non nul)
     * @return La partie entière de la division de x par y
     * @throws IllegalArgumentException si x est négatif ou y est nul ou négatif
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);

        return (x + y - 1) / y;
    }

    /**
     * Retourne la valeur du point sur l'axe y passant par la droite (0,y0) et (1,y1) du point x.
     *
     * @param y0 Le premier point passant par la droite
     * @param y1 Le deuxième point passant par la droite
     * @param x  La valeur du point dont on veut trouver l'ordonnée.
     * @return L'ordonnée du point x passant par la droite formé par y0 et y1
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Retourne la valeur (int) la plus proche de v dans l'intervalle entre min et max.
     *
     * @param min Le minimum de l'intervalle (ne peut pas être supérieur a max)
     * @param max Le maximum de l'intervalle (ne peut pas être inférieur a min)
     * @param v   La valeur (int)
     * @return La valeur (int) la plus proche de v compris entre min et max
     * @throws IllegalArgumentException si min est inférieur a max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min < max);

        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        }

        return v;
    }

    /**
     * Retourne la valeur (double) la plus proche de v dans l'intervalle entre min et max
     *
     * @param min Le minimum de l'intervalle (ne peut pas être supérieur a max)
     * @param max Le maximum de l'intervalle (ne peut pas être inférieur a min)
     * @param v   La valeur (double)
     * @return la valeur (double) la plus proche de v compris entre min et max
     * @throws IllegalArgumentException si min est inférieur a max
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min < max);

        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        }

        return v;
    }

    /**
     * Retourne le sinus hyperbolique inverse de x.
     *
     * @param x la coordonnée x
     * @return le sinus hyperbolique inverse de x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Retourne le produit scalar entre un vecteur u et v
     *
     * @param uX La coordonnée x du vecteur u
     * @param uY La coordonnée y du vecteur u
     * @param vX La coordonnée x du vecteur v
     * @param vY La coordonnée y du vecteur v
     * @return le produit scalaire entre u et v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, uY * vY);
    }

    /**
     * Retourne le carré de la norme du vecteur u
     *
     * @param uX La coordonnée x du vecteur u
     * @param uY La coordonnée y du vecteur u
     * @return Le carré de la norme du vecteur u
     */
    public static double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    /**
     * Retourne la norme du vecteur u
     *
     * @param uX La coordonnée x du vecteur u
     * @param uY La coordonnée y du vecteur u
     * @return La norme du vecteur u
     */
    public static double norm(double uX, double uY) {
        return Math.hypot(uX, uY);
    }

    /**
     * Retourne la longueur de la projection du vecteur AP sur la droite AB
     *
     * @param aX La coordonnée x du point a
     * @param aY La coordonnée y du point a
     * @param bX La coordonnée x du point b
     * @param bY La coordonnée y du point b
     * @param pX La coordonnée x du point p
     * @param pY La coordonnée y du point p
     * @return la longueur de la projection du vecteur AP sur la droite AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;
        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }

}
