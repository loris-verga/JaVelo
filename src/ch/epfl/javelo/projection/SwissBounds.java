package ch.epfl.javelo.projection;

/**
 *
 * La classe Swissbounds non instantiable contient des constantes et des méthodes liées aux limites de la Suisse.
 *
 * @author Loris Verga (345661)
 */
public final class SwissBounds {

    //Constructeur privé de sorte que la classe ne soit pas instantiable.
    private SwissBounds(){}

    //Liste de constantes utiles :

    //la plus petite coordonnée E de Suisse
    public final static double MIN_E = 2485000;

    //la plus grande coordonnée E de Suisse
    public final static double MAX_E = 2834000;

    //la plus petite coordonnée N de Suisse
    public final static double MIN_N = 1075000;

    //la plus grande coordonnée N de Suisse
    public final static double MAX_N = 1296000;

    //la largeur de la Suisse en mètres, définie comme la différence entre MAX_E et MIN_E
    public final static double WIDTH = MAX_E - MIN_E;

    //la hauteur de la Suisse en mètres, définie comme la différence entre MAX_N et MIN_N.
    public final static double HEIGHT = MAX_N - MIN_N;


    /**
     * La méthode containsEN, publique et statique permet de tester si un point (des coordonnées) se trouve
     * dans les limites de la Suisse.
     *
     * @param e La coordonnée Est du point dont on veut déterminer s'il se trouve en Suisse.
     * @param n La coordonnée Nord du point dont on veut déterminer s'il se trouve en Suisse.
     * @return Retourne true si le point se trouve en Suisse.
     */
    public static boolean containsEN(double e, double n) {
        return e >= MIN_E && e <= MAX_E && n >= MIN_N && n <= MAX_N;
    }
}
