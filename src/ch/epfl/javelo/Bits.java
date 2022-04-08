package ch.epfl.javelo;

/**
 * La classe Bits offre l'extraction d'une séquence de Bits d'un vecteur de 32 bits
 *
 * @author Juan B Iaconucci (342153)
 */

public final class Bits {
    private Bits() {
    }

    /**
     * La méthode extractSigned retourne depuis value, la plage de length bits commençant au bit d'index start,
     * en l'interprétant comme valeur signée en complément a deux.
     *
     * @param value  vecteur de 32 bits qu'on veut extraire une valeur
     * @param start  le début de la plage de bit qu'on veut extraire
     * @param length la longueur de la plage de bit qu
     * @return la plage de length bits
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(0 <= start
                && start < Integer.SIZE
                && 0 <= start + length
                && start + length <= Integer.SIZE
                && length >= 0);

        if (length == 0) {
            return 0;
        }

        int leftShift = Integer.SIZE - (start + length);
        int rightShift = start + leftShift;

        int leftShiftedValue = value << leftShift;

        return leftShiftedValue >> rightShift;
    }

    /**
     * La méthode extractUnsigned retourne depuis value, la plage de length bits commençant au bit d'index start,
     * en l'interprétant comme valeur non signée en complément a deux
     *
     * @param value  vecteur de 32 bits qu'on veut extraire une valeur
     * @param start  le début de la plage de bit qu'on veut extraire
     * @param length la longueur de la plage de bit qu
     * @return la plage de length bits
     */
    public static int extractUnsigned(int value, int start, int length) {

        Preconditions.checkArgument(0 <= start
                && start < Integer.SIZE
                && 0 <= start + length
                && start + length <= Integer.SIZE
                && length >= 0
                && length < Integer.SIZE);

        if (length == 0) {
            return 0;
        }

        int leftShift = Integer.SIZE - (start + length);
        int rightShift = start + leftShift;

        int leftShiftedValue;
        int finalValue;

        leftShiftedValue = value << leftShift;

        finalValue = leftShiftedValue >>> rightShift;

        return finalValue;

    }
}
