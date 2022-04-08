package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * La classe Functions, publique, finale et non instantiable, contient des méthodes
 * permettant de créer des objets représentant des fonctions mathématiques des réels vers les réels.
 *
 * @author Loris Verga (345661)
 */
public final class Functions {

    //Constructeur privé de sorte que la classe ne soit pas instantiable.
    private Functions(){};


    /**
     * Cette méthode retourne une fonction constante dont la valeur est toujours y
     *
     * @param y valeur constante de la fonction
     * @return retourne une fonction dont la valeur est toujours y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);

    }

    /**
     * Sous-classe Constant : Sert à définir la fonction constante
     */
    private static final class Constant implements DoubleUnaryOperator {

        private final double constante;

        private Constant(double constante) {
            this.constante = constante;

        }

        @Override
        public double applyAsDouble(double x) {
            return this.constante;

        }
    }


    /**
     * Méthode DoubleUnaryOperator
     * Cette méthode retourne une fonction obtenue par interpolation linéaire entre les échantillons samples,
     * espacés régulièrement et couvrant la plage allant de 0 à xMax.
     *
     * @param samples tableau de sample
     * @param xMax    valeur maximale
     * @return La fonction
     * @throws IllegalArgumentException si le tableau samples contient moins de deux éléments, ou si xMax est inférieur ou égal à 0.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        return new Sampled(samples, xMax);
    }


    private static final class Sampled implements DoubleUnaryOperator {
        private final float[] sampled;
        private final double xMax;

        public Sampled(float[] sampled, double xMax) {
            this.sampled = sampled.clone();
            this.xMax = xMax;
            Preconditions.checkArgument(this.sampled.length >= 2 && xMax > 0);
        }

        @Override
        public double applyAsDouble(double x) {

            if (x <= 0) {
                return sampled[0];
            }
            if (x >= xMax) {
                return sampled[sampled.length - 1];
            } else {

                double tailleIntervalle = xMax / (sampled.length - 1);
                double indexX = x / tailleIntervalle;

                int valeurBasse = (int) Math.floor(indexX);
                int valeurHaute = (int) Math.ceil(indexX);

                return Math2.interpolate(sampled[valeurBasse], sampled[valeurHaute], indexX - Math.floor(indexX));

            }
        }
    }
}
