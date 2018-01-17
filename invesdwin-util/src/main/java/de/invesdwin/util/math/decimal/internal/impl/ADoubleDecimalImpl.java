package de.invesdwin.util.math.decimal.internal.impl;

import java.io.IOException;
import java.math.RoundingMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public abstract class ADoubleDecimalImpl<E extends ADoubleDecimalImpl<E>> extends ADecimalImpl<E> {

    private static final double NULL_VALUE = Double.NaN;

    private double value;

    @GuardedBy("none for performance")
    private transient double defaultRoundedValue;

    public ADoubleDecimalImpl(final Double value) {
        this(nullToNaN(value));
    }

    public ADoubleDecimalImpl(final double value) {
        if (isNullValue(value)) {
            this.value = getZero();
            this.defaultRoundedValue = this.value;
        } else {
            this.value = value;
            this.defaultRoundedValue = NULL_VALUE;
        }
    }

    private static double nullToNaN(final Double value) {
        if (value == null) {
            return NULL_VALUE;
        } else {
            return value;
        }
    }

    private static boolean isNullValue(final double value) {
        return Double.isNaN(value);
    }

    protected abstract double internalRound(double value, int scale, RoundingMode roundingMode);

    protected abstract double getZero();

    protected final double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(getDefaultRoundedValue());
    }

    @Override
    public boolean equals(final ADecimal<?> other) {
        return internalCompareTo(other) == 0;
    }

    @Override
    public boolean equals(final Object other) {
        return compareTo(other) == 0;
    }

    @Override
    public E round(final int scale, final RoundingMode roundingMode) {
        if (roundingMode == RoundingMode.UNNECESSARY) {
            return getGenericThis();
        }
        if (scale == Decimal.DEFAULT_ROUNDING_SCALE && roundingMode == Decimal.DEFAULT_ROUNDING_MODE) {
            final double rounded = getDefaultRoundedValue();
            return newValueCopy(rounded).setAlreadyDefaultRounded(true);
        } else {
            final double rounded = internalRound(value, scale, roundingMode);
            return newValueCopy(rounded);
        }
    }

    /**
     * this value should be used for comparisons
     */
    protected final double getDefaultRoundedValue() {
        if (isNullValue(defaultRoundedValue)) {
            defaultRoundedValue = internalRound(value, Decimal.DEFAULT_ROUNDING_SCALE, Decimal.DEFAULT_ROUNDING_MODE);
        }
        return defaultRoundedValue;
    }

    @Override
    public E setAlreadyDefaultRounded(final boolean alreadyDefaultRounded) {
        if (alreadyDefaultRounded) {
            defaultRoundedValue = value;
        }
        return getGenericThis();
    }

    @Override
    public boolean isAlreadyDefaultRounded() {
        return !isNullValue(defaultRoundedValue);
    }

    protected abstract E newValueCopy(double value);

    private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
        stream.writeDouble(value);
    }

    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        value = stream.readDouble();
        defaultRoundedValue = NULL_VALUE;
    }

}
