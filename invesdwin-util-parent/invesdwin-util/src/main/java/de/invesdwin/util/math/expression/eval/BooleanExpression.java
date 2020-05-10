package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;

@Immutable
public class BooleanExpression extends ConstantExpression {

    public BooleanExpression(final boolean value) {
        super(Doubles.checkedCast(value));
    }

    @Override
    public String toString() {
        return String.valueOf(evaluateBooleanNullable());
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
