package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class EnumerationExpression extends ConstantExpression {

    private final String name;

    public EnumerationExpression(final String name, final double value) {
        super(value);
        this.name = name.intern();
    }

    @Override
    public String toString() {
        return name;
    }

    public static EnumerationExpression valueOf(final Enum<?> enumerationValue) {
        return new EnumerationExpression(enumerationValue.name(), enumerationValue.ordinal());
    }

    public static EnumerationExpression[] valueOf(final Enum<?>[] enumerationValues) {
        final EnumerationExpression[] values = new EnumerationExpression[enumerationValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = valueOf(enumerationValues[i]);
        }
        return values;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EnumerationExpression) {
            final EnumerationExpression cObj = (EnumerationExpression) obj;
            return name == cObj.name;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ConstantExpression.class.hashCode() + name.hashCode();
    }

}
