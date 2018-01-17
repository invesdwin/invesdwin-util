package de.invesdwin.util.math.decimal.internal.impl;

import java.math.RoundingMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public abstract class AGenericDecimalImpl<E extends AGenericDecimalImpl<E, V>, V> extends ADecimalImpl<E> {

    private final V value;

    @GuardedBy("none for performance")
    private transient V defaultRoundedValue;

    public AGenericDecimalImpl(final V value) {
        if (value == null) {
            this.value = getZero();
            this.defaultRoundedValue = this.value;
        } else {
            this.value = value;
            this.defaultRoundedValue = null;
        }
    }

    protected abstract V internalRound(V value, int scale, RoundingMode roundingMode);

    protected abstract V getZero();

    protected final V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return getDefaultRoundedValue().hashCode();
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
    public ADecimalImpl<E> round(final int scale, final RoundingMode roundingMode) {
        if (roundingMode == RoundingMode.UNNECESSARY) {
            return getGenericThis();
        }
        if (scale == Decimal.DEFAULT_ROUNDING_SCALE && roundingMode == Decimal.DEFAULT_ROUNDING_MODE) {
            final V rounded = getDefaultRoundedValue();
            return newValueCopy(rounded).setAlreadyDefaultRounded(true);
        } else {
            final V rounded = internalRound(value, scale, roundingMode);
            return newValueCopy(rounded);
        }
    }

    /**
     * this value should be used for comparisons
     */
    protected final V getDefaultRoundedValue() {
        if (defaultRoundedValue == null) {
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
        return defaultRoundedValue != null;
    }

    protected abstract E newValueCopy(V value);

}
