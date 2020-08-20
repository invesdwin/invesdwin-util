package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionType;

@NotThreadSafe
public class EnumerationExpression extends ConstantExpression {

    private final String name;

    public EnumerationExpression(final String name, final double value) {
        super(value, ExpressionType.Integer);
        this.name = name;
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

}
