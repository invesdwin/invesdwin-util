package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;

@Immutable
public class DoubleDecimal extends ADecimalNumber<DoubleDecimal> {

    private final double value;

    public DoubleDecimal(final double value) {
        this.value = value;
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof Number) {
            final Number cO = (Number) o;
            return Doubles.compare(value, cO.doubleValue());
        } else {
            return 1;
        }
    }

    @Override
    public DoubleDecimal fromDefaultValue(final double value) {
        return new DoubleDecimal(value);
    }

    @Override
    public double getDefaultValue() {
        return value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Number) {
            final Number cObj = (Number) obj;
            return Doubles.equals(value, cObj.doubleValue());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    public static DoubleDecimal valueOf(final double value) {
        return new DoubleDecimal(value);
    }

}
