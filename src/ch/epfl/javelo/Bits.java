package ch.epfl.javelo;

/** Classe Bits
 *
 * La classe Bits offre l'extraction d'une séquence de Bits d'un vecteur de 32 bits
 *
 * @author Juan B Iaconucci (342153)
 */

public final class Bits {
    private Bits() {}

    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(0<=start && start<=31 && 0<=start+length && start+length<=31);
        int leftShift = start + length;
        int rightShift = start + leftShift;
        int newValue;
        //todo go to course, archive, week 10, look at videos about bits

        //Décalage de la plage de bit vers la gauche
        newValue = (int) (value * Math.pow(10 , leftShift));

        //ejectage des bits supplementaire
        newValue = (int) (newValue % Math.pow(10 , 32));

        //Décalage de la plage de bit vers la droite et ejectage des bits supplementaire
        newValue = (int) Math.floor(newValue / (int)(Math.pow(10 , rightShift)));

        return newValue;
    }

    public static int extractUnsigned(int value, int start, int length){


        return value;
    }

}
