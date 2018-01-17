package de.invesdwin.util.math.decimal.internal.impl;

import java.math.RoundingMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public abstract class ADoubleDecimalImpl<E extends ADoubleDecimalImpl<E>> extends ADecimalImpl<E> {

    private static final double NULL_VALUE = Double.NaN;

    private final double value;

    @GuardedBy("none for performance")
    private transient double defaultRoundedValue;
    @GuardedBy("none for performance")
    private transient boolean defaultRoundedValueActuallyRounded;

    public ADoubleDecimalImpl(final Double value, final Double defaultRoundedValue) {
        this(nullToNaN(value), nullToNaN(defaultRoundedValue));
    }

    public ADoubleDecimalImpl(final double value, final double defaultRoundedValue) {
        if (isNullValue(value)) {
            this.value = getZero();
            this.defaultRoundedValue = this.value;
            this.defaultRoundedValueActuallyRounded = true;
        } else {
            this.value = value;
            this.defaultRoundedValue = defaultRoundedValue;
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
        final double rounded;
        if (scale == Decimal.DEFAULT_ROUNDING_SCALE && roundingMode == Decimal.DEFAULT_ROUNDING_MODE) {
            if (!defaultRoundedValueActuallyRounded) {
                defaultRoundedValue = NULL_VALUE;
            }
            rounded = getDefaultRoundedValue();
        } else {
            rounded = internalRound(value, scale, roundingMode);
        }
        return newValueCopy(rounded, rounded);
    }

    /**
     * this value should be used for comparisons
     */
    protected final double getDefaultRoundedValue() {
        if (isNullValue(defaultRoundedValue)) {
            defaultRoundedValue = internalRound(value, Decimal.DEFAULT_ROUNDING_SCALE, Decimal.DEFAULT_ROUNDING_MODE);
            defaultRoundedValueActuallyRounded = true;
        }
        return defaultRoundedValue;
    }

    protected final E newValueCopy(final double value) {
        return newValueCopy(value, NULL_VALUE);
    }

    protected abstract E newValueCopy(double value, double defaultRoundedValue);

}
