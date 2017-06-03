package de.invesdwin.util.math.decimal.internal.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.norva.marker.IDecimal;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

/**
 * If null values are put into this, they are automatically converted to 0.
 * 
 * This class does not extend AValueObject to improve performance by skipping the bean property aspect.
 */
@ThreadSafe
public abstract class ADecimalImpl<E extends ADecimalImpl<E, V>, V>
        implements Comparable<Object>, Serializable, IDecimal {

    private final V value;

    @GuardedBy("none for performance")
    private transient Integer wholeNumberDigits;
    @GuardedBy("none for performance")
    private transient Integer decimalDigits;
    @GuardedBy("none for performance")
    private transient Integer digits;
    @GuardedBy("none for performance")
    private transient String toString;
    @GuardedBy("none for performance")
    private transient V defaultRoundedValue;
    @GuardedBy("none for performance")
    private transient boolean defaultRoundedValueActuallyRounded;

    public ADecimalImpl(final V value, final V defaultRoundedValue) {
        if (value == null) {
            this.value = getZero();
            this.defaultRoundedValue = this.value;
            this.defaultRoundedValueActuallyRounded = true;
        } else {
            this.value = value;
            this.defaultRoundedValue = defaultRoundedValue;
        }
    }

    protected abstract V internalRound(V value, int scale, RoundingMode roundingMode);

    protected abstract V getZero();

    protected final V getValue() {
        return value;
    }

    public int getWholeNumberDigits() {
        if (wholeNumberDigits == null) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int indexOfDecimalPoint = s.indexOf(".");
            if (indexOfDecimalPoint != -1) {
                wholeNumberDigits = indexOfDecimalPoint;
            } else {
                wholeNumberDigits = Math.max(1, s.length());
            }
        }
        return wholeNumberDigits;
    }

    /**
     * Returns the real scale without trailing zeros.
     */
    public int getDecimalDigits() {
        if (decimalDigits == null) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int indexOfDecimalPoint = s.indexOf(".");
            if (indexOfDecimalPoint != -1) {
                decimalDigits = s.length() - indexOfDecimalPoint - 1;
            } else {
                decimalDigits = 0;
            }
        }
        return decimalDigits;
    }

    public int getDigits() {
        if (digits == null) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int indexOfDecimalPoint = s.indexOf(".");
            if (indexOfDecimalPoint != -1) {
                digits = s.length() - 1;
            } else {
                digits = Math.max(1, s.length());
            }
        }
        return digits;
    }

    public abstract boolean isZero();

    /**
     * 0 is counted as positive aswell here.
     */
    public abstract boolean isPositive();

    @Override
    public int hashCode() {
        return getDefaultRoundedValue().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(final Object other) {
        final ADecimal<?> decimalOther;
        if (other instanceof ADecimal) {
            decimalOther = (ADecimal<?>) other;
        } else if (other instanceof Number) {
            final Number cOther = (Number) other;
            decimalOther = Decimal.valueOf(cOther);
        } else {
            return 1;
        }
        return internalCompareTo(decimalOther);
    }

    protected abstract int internalCompareTo(ADecimal<?> other);

    @Override
    public String toString() {
        if (toString == null) {
            final String s = internalToString();
            if (s.length() > 1 && s.contains(".")) {
                toString = Strings.removeEnd(Strings.removeTrailing(s, "0"), ".");
            } else {
                toString = s;
            }
        }
        return toString;
    }

    protected abstract String internalToString();

    public abstract E abs();

    public abstract E scaleByPowerOfTen(int n);

    public abstract E root(Number n);

    public abstract E root(ADecimal<?> n);

    public abstract E pow(Number exponent);

    public abstract E pow(ADecimal<?> exponent);

    public abstract E subtract(final ADecimal<?> subtrahend);

    public abstract E add(final ADecimal<?> augend);

    public abstract E multiply(final Number multiplicant);

    public abstract E multiply(final ADecimal<?> multiplicant);

    public abstract E divide(final Number divisor);

    public abstract E divide(final ADecimal<?> divisor);

    public abstract E remainder(final Number divisor);

    public abstract E remainder(final ADecimal<?> divisor);

    public E round(final int scale, final RoundingMode roundingMode) {
        if (roundingMode == RoundingMode.UNNECESSARY) {
            return getGenericThis();
        }
        final V rounded;
        if (scale == Decimal.DEFAULT_ROUNDING_SCALE && roundingMode == Decimal.DEFAULT_ROUNDING_MODE) {
            if (!defaultRoundedValueActuallyRounded) {
                defaultRoundedValue = null;
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
    protected final V getDefaultRoundedValue() {
        if (defaultRoundedValue == null) {
            defaultRoundedValue = internalRound(value, Decimal.DEFAULT_ROUNDING_SCALE, Decimal.DEFAULT_ROUNDING_MODE);
            defaultRoundedValueActuallyRounded = true;
        }
        return defaultRoundedValue;
    }

    protected final E newValueCopy(final V value) {
        return newValueCopy(value, null);
    }

    protected abstract E newValueCopy(V value, V defaultRoundedValue);

    protected abstract E getGenericThis();

    public abstract E sqrt();

    public abstract BigDecimal bigDecimalValue();

    public abstract BigInteger bigIntegerValue();

    public abstract int intValue();

    public abstract long longValue();

    public abstract float floatValue();

    public abstract double doubleValue();

    /**
     * This gives the raw value, thus not getting rounded for precision.
     */
    public abstract double doubleValueRaw();

    public abstract byte byteValue();

    public abstract short shortValue();

    public abstract Dfp dfpValue();

    public abstract Number numberValue();

    public abstract E log();

    public abstract E exp();

    public abstract E log10();

    public abstract E exp10();

    public abstract E cos();

    public abstract E sin();

}
