package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import org.w3c.dom.Attr;

import java.util.StringJoiner;

/**
 * L'enregistrement AttributeSet représente un ensemble d'attributs OpenStreetMap
 *
 * @author Juan B Iaconucci (342153)
 */
public record AttributeSet(long bits)  {

    public AttributeSet {
        Preconditions.checkArgument( 0 < bits && bits < 0b100000000000000000000000000000000000000000000000000000000000000L);
    }

    public static AttributeSet of(Attribute... attributes) {
        long value = 0L;
        for (Attribute a : attributes) {
            long mask = 1L << a.ordinal();
            value = value + mask;
        }
        return (new AttributeSet(value));
    }

    public boolean contains(Attribute attribute){

        long value = (this.bits() >>> attribute.ordinal()) & 1L;

        return (value == 1L);
    }

    public boolean intersects(AttributeSet that){

        long value = this.bits() & that.bits();

        return (value != 0L);
    }

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
