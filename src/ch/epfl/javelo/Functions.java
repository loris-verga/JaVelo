package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * La classe Functions, publique, finale et non instantiable, contient des méthodes
 * permettant de créer des objets représentant des fonctions mathématiques des réels vers les réels.
 *
 * @author Loris Verga (345661)
 */
public final class Functions {


    /**
     * Cette méthode retourne une fonction constante dont la valeur est toujours y
     * @param y valeur constante de la fonction
     * @return retourne une fonction dont la valeur est toujours y
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);

    }

    /**
     * Sous-classe Constant : Sert à définir la fonction constante
     */
    private static final class Constant implements DoubleUnaryOperator {

        private double constante;

        public Constant(double constante){
            this.constante = constante;

        }
        @Override
        public double applyAsDouble(double x){
            return this.constante;

    }
}


    /**
     * Méthode DoubleUnaryOperator
     * Cette méthode retourne une fonction obtenue par interpolation linéaire entre les échantillons samples,
     * espacés régulièrement et couvrant la plage allant de 0 à xMax.
     * @throws IllegalArgumentException si le tableau samples contient moins de deux éléments, ou si xMax est inférieur ou égal à 0.
     * @param samples
     * @param xMax
     * @return
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        return null;
    }


    private static final class Sampled implements DoubleUnaryOperator{
        private float[] sampled;
        private double xMax;
        public Sampled(float[] sampled, double xMax){
            this.sampled = sampled.clone();
            this.xMax = xMax;
        }

        @Override
        public double applyAsDouble(double x){



            return 0;
        }

    }


}
