package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * L'enregistrement AttributeSet représente un ensemble d'attributs OpenStreetMap.
 *
 * @author Juan B Iaconucci (342153)
 */
public record AttributeSet(long bits)  {

    /**
     * Constructeur d'AttributeSet
     *
     * @param bits représente le contenu de l'ensemble AttributeSet
     * @throw si bits est strictement inférieur à 0 ou si bits est supérieur à 2 à la puissance 62
     */
    public AttributeSet {
        Preconditions.checkArgument( 0 <= bits && bits < 0b100000000000000000000000000000000000000000000000000000000000000L);
    }

    /**
     * Méthode de construction d'AttributeSet
     *
     * @param attributes les attributs que l'on veut dans l'ensemble
     * @return l'ensemble qui contient tous les attributs en argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long value = 0L;
        for (Attribute a : attributes) {
            long mask = 1L << a.ordinal();
            value = value + mask;
        }
        return (new AttributeSet(value));
    }

    /**
     * La méthode contains retourne true si l'attribut en argument est dans l'ensemble.
     *
     * @param attribute l'attribut donné
     * @return true si l'attribut en argument est dans l'ensemble
     */
    public boolean contains(Attribute attribute){

        long value = (this.bits() >>> attribute.ordinal()) & 1L;

        return (value == 1L);
    }

    /**
     * La méthode intersects retourne true si l'intersection entre l'ensemble récepteur (this) avec celui passé en argument (that) n'est pas vide.
     *
     * @param that l'ensemble qu'on veut comparer
     * @return true si l'intersection n'est pas vide
     */
    public boolean intersects(AttributeSet that){

        long value = this.bits() & that.bits();

        return (value != 0L);
    }

    /**
     * Méthode qui retourne une chaîne composée de la représentation textuelle de tous les attributs de l'ensemble.
     *
     * @return une chaîne composée de la représentation textuelle de tous les attributs dans l'ensemble
     */
    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        long bitsValue = this.bits();
        for (int i = 0; i <= 63; i += 1) {
            if ((bitsValue >>> i) % 2 == 1) {
                joiner.add(Attribute.ALL.get(i).keyValue());
            }
        }
        return joiner.toString();
    }
}
