package de.invesdwin.util.math.decimal.internal.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.norva.marker.IDecimal;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

/**
 * If null values are put into this, they are automatically converted to 0.
 * 
 * This class does not extend AValueObject to improve performance by skipping the bean property aspect.
 */
@ThreadSafe
public abstract class ADecimalImpl<E extends ADecimalImpl<E>> implements Comparable<Object>, Serializable, IDecimal {

    /**
     * Wrap this information in a value object since not many decimals require this info. This saves a lot of heap
     * space.
     */
    private transient DecimalDigitsInfo digitsInfo;

    private DecimalDigitsInfo getDigitsInfo() {
        if (digitsInfo == null) {
            digitsInfo = new DecimalDigitsInfo(internalToString());
        }
        return digitsInfo;
    }

    public int getWholeNumberDigits() {
        return getDigitsInfo().getWholeNumberDigits();
    }

    /**
     * Returns the real scale without trailing zeros.
     */
    public int getDecimalDigits() {
        return getDigitsInfo().getDecimalDigits();
    }

    public int getDigits() {
        return getDigitsInfo().getDigits();
    }

    public abstract boolean isZero();

    /**
     * 0 is counted as positive aswell here.
     */
    public abstract boolean isPositive();

    @Override
    public abstract int hashCode();

    public abstract boolean equals(ADecimal<?> other);

    @Override
    public abstract boolean equals(Object other);

    @Override
    public int compareTo(final Object other) {
        final ADecimal<?> decimalOther;
        if (other instanceof ADecimal) {
            decimalOther = (ADecimal<?>) other;
        } else if (other instanceof Number) {
            final Number cOther = (Number) other;
            decimalOther = Decimal.valueOf(cOther);
        } else if (other instanceof ADecimalImpl) {
            throw new IllegalArgumentException("Please provide the " + ADecimal.class.getSimpleName()
                    + " instead of the " + ADecimalImpl.class.getSimpleName() + " for comparison: "
                    + other.getClass().getSimpleName() + "[" + other + "]");
        } else {
            return 1;
        }
        return internalCompareTo(decimalOther);
    }

    protected abstract int internalCompareTo(ADecimal<?> other);

    @Override
    public String toString() {
        return getDigitsInfo().toString();
    }

    protected abstract String internalToString();

    public abstract E abs();

    public abstract E scaleByPowerOfTen(int n);

    public abstract E root(Number n);

    public abstract E root(ADecimal<?> n);

    public abstract E pow(Number exponent);

    public abstract E pow(ADecimal<?> exponent);

    public abstract E subtract(ADecimal<?> subtrahend);

    public abstract E add(ADecimal<?> augend);

    public abstract E multiply(Number multiplicant);

    public abstract E multiply(ADecimal<?> multiplicant);

    public abstract E divide(Number divisor);

    public abstract E divide(ADecimal<?> divisor);

    public abstract E remainder(Number divisor);

    public abstract E remainder(ADecimal<?> divisor);

    public abstract ADecimalImpl<E> round(int scale, RoundingMode roundingMode);

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

    public abstract E setAlreadyDefaultRounded(boolean alreadyRounded);

    public abstract boolean isAlreadyDefaultRounded();

}
