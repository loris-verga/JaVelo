package ch.epfl.javelo;

/** Classe Bits
 *
 * La classe Bits offre l'extraction d'une séquence de Bits d'un vecteur de 32 bits
 *
 * @author Juan B Iaconucci (342153)
 */

public final class Bits {
    private Bits() {}

    /**
     * Retourne depuis value, la plage de length bits commençant au bit d'index start,
     * en l'interprétant comme valeur signée en complément a deux
     *
     * @param value vecteur de 32 bits qu'on veut extraire une valeur
     * @param start le début de la plage de bit qu'on veut extraire
     * @param length la longueur de la plage de bit qu
     * @return la plage de length bits
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(0<=start && start<=31 && 0<=start+length && start+length<=32 && length>=0);

        if (length == 0){return 0;}

        int leftShift = 32 - (start + length);
        int rightShift = start + leftShift;

        //Décalage de la plage de bit vers la gauche
        int leftShiftedValue = value << leftShift;

        //Décalage arithmétique de la plage de bit vers la droite
        int finalValue = leftShiftedValue >> rightShift;

        return finalValue;
    }

    /**
     * Retourne depuis value, la plage de length bits commençant au bit d'index start,
     * en l'interprétant comme valeur non signée en complément a deux
     *
     * @param value vecteur de 32 bits qu'on veut extraire une valeur
     * @param start le début de la plage de bit qu'on veut extraire
     * @param length la longueur de la plage de bit qu
     * @return la plage de length bits
     */
    public static int extractUnsigned(int value, int start, int length){

        Preconditions.checkArgument(0<=start && start<=31 && 0<=start+length && start+length<=32 && length>=0 && length<32);

        if (length == 0){return 0;}

        int leftShift = 32 - (start + length);
        int rightShift = start + leftShift;

        int leftShiftedValue;
        int finalValue;

        //Décalage de la plage de bit vers la gauche
        leftShiftedValue = value << leftShift;

        //Décalage logique de la plage de bit vers la droite
        finalValue = leftShiftedValue >>> rightShift;

        return finalValue;

    }

}
