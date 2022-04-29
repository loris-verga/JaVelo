package ch.epfl.javelo;


/**
 * La classe Preconditions permet de vérifier les arguments passés aux méthodes.
 *
 * @author Loris Verga (3455661)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * La méthode checkArgument permet de vérifier la validité des arguments d'une méthode.
     *
     * @param shouldBeTrue correspond à la condition pour que le paramètre d'une méthode soit accepté,
     *                     si cette condition n'est pas respectée, la méthode checkArgument
     *                     enverra une IllegalArgumentException.
     * @throws IllegalArgumentException si la condition que le paramètre d'une méthode soit accepté est fausse.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}