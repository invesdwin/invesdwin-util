package de.invesdwin.util.math.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
@ThreadSafe
class ScaledDecimalDelegateImpl extends ADecimalImpl {

    private final AScaledDecimal<?, ?> parent;
    private final ADecimalImpl delegate;
    @GuardedBy("none for performance")
    private transient ADecimalImpl defaultScaledDelegate;

    ScaledDecimalDelegateImpl(final AScaledDecimal<?, ?> parent, final ADecimalImpl delegate) {
        super(false, false);
        this.parent = parent;
        if (delegate instanceof ScaledDecimalDelegateImpl) {
            throw new IllegalArgumentException("Delegate [" + delegate + "] should not be an instance of "
                    + ScaledDecimalDelegateImpl.class.getSimpleName());
        }
        this.delegate = delegate;
    }

    public ADecimalImpl getDelegate() {
        return delegate;
    }

    private ADecimalImpl getDefaultScaledDelegate() {
        if (defaultScaledDelegate == null) {
            defaultScaledDelegate = parent.getDefaultValue().getImpl();
        }
        return defaultScaledDelegate;
    }

    @Override
    public ADecimalImpl round(final int scale, final RoundingMode roundingMode) {
        return getDelegate().round(scale, roundingMode);
    }

    @Override
    public int getWholeNumberDigits() {
        return getDefaultScaledDelegate().getWholeNumberDigits();
    }

    @Override
    public int getDecimalDigits() {
        return getDefaultScaledDelegate().getDecimalDigits();
    }

    @Override
    public int getDigits() {
        return getDefaultScaledDelegate().getDigits();
    }

    @Override
    public boolean isZero() {
        return getDelegate().isZero();
    }

    @Override
    public boolean isPositive() {
        return getDelegate().isPositive();
    }

    @Override
    public int hashCode() {
        return getDefaultScaledDelegate().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        final Object comparableOther = getComparableOther(other);
        return getDefaultScaledDelegate().equals(comparableOther);
    }

    @Override
    public int compareTo(final Object other) {
        final Object comparableOther = getComparableOther(other);
        return getDefaultScaledDelegate().compareTo(comparableOther);
    }

    @Override
    protected int internalCompareTo(final ADecimal decimalOther) {
        throw new UnsupportedOperationException();
    }

    private Object getComparableOther(final Object other) {
        final Object comparableOther;
        if (other instanceof AScaledDecimal) {
            final AScaledDecimal<?, ?> cOther = (AScaledDecimal<?, ?>) other;
            comparableOther = cOther.getDefaultValue();
        } else {
            comparableOther = other;
        }
        return comparableOther;
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public ADecimalImpl abs() {
        return getDelegate().abs();
    }

    @Override
    public ADecimalImpl scaleByPowerOfTen(final int n) {
        return getDelegate().scaleByPowerOfTen(n);
    }

    @Override
    public ADecimalImpl root(final Number n) {
        return getDelegate().root(n);
    }

    @Override
    public ADecimalImpl root(final ADecimal n) {
        return getDelegate().root(n);
    }

    @Override
    public ADecimalImpl sqrt() {
        return getDelegate().sqrt();
    }

    @Override
    public ADecimalImpl pow(final Number exponent) {
        return getDelegate().pow(exponent);
    }

    @Override
    public ADecimalImpl pow(final ADecimal exponent) {
        return getDelegate().pow(exponent);
    }

    @Override
    public ADecimalImpl multiply(final Number multiplicant) {
        final ADecimalImpl newDefault = getDelegate().multiply(multiplicant);
        return parent.newValueCopy(newDefault).getImpl();
    }

    @Override
    public ADecimalImpl divide(final Number divisor) {
        final ADecimalImpl newDefault = getDelegate().divide(divisor);
        return parent.newValueCopy(newDefault).getImpl();
    }

    @Override
    public ADecimalImpl remainder(final Number divisor) {
        final ADecimalImpl newDefault = getDelegate().remainder(divisor);
        return parent.newValueCopy(newDefault).getImpl();
    }

    @Override
    @Deprecated
    public ADecimalImpl subtract(final ADecimal subtrahend) {
        //        final ADecimal<?> defaultScaledSubtrahend = maybeGetDefaultScaledNumber(subtrahend);
        //        final ADecimalImpl newDefault = getDefaultScaledDelegate().subtract(defaultScaledSubtrahend);
        //        return parent.fromDefaultValue(new Decimal(newDefault)).getImpl();
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ADecimalImpl add(final ADecimal augend) {
        //        final ADecimal<?> defaultScaledAugend = maybeGetDefaultScaledNumber(augend);
        //        final ADecimalImpl newDefault = getDefaultScaledDelegate().add(defaultScaledAugend);
        //        return parent.fromDefaultValue(new Decimal(newDefault)).getImpl();
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ADecimalImpl multiply(final ADecimal multiplicant) {
        //        final ADecimal<?> defaultScaledMultiplicant = maybeGetDefaultScaledNumber(multiplicant);
        //        final ADecimalImpl newDefault = getDefaultScaledDelegate().multiply(defaultScaledMultiplicant);
        //        return parent.fromDefaultValue(new Decimal(newDefault)).getImpl();
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ADecimalImpl divide(final ADecimal divisor) {
        //        final ADecimal<?> defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
        //        final ADecimalImpl newDefault = getDefaultScaledDelegate().divide(defaultScaledDivisor);
        //        return parent.fromDefaultValue(new Decimal(newDefault)).getImpl();
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ADecimalImpl remainder(final ADecimal divisor) {
        //        final ADecimal<?> defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
        //        final ADecimalImpl newDefault = getDefaultScaledDelegate().remainder(defaultScaledDivisor);
        //        return parent.fromDefaultValue(new Decimal(newDefault)).getImpl();
        throw new UnsupportedOperationException();
    }

    //    private ADecimal<?> maybeGetDefaultScaledNumber(final ADecimal<?> number) {
    //        if (number instanceof AScaledDecimal) {
    //            final AScaledDecimal<?, ?> scaledNumber = (AScaledDecimal<?, ?>) number;
    //            return scaledNumber.getDefaultValue();
    //        } else {
    //            return number;
    //        }
    //    }

    @Override
    public BigDecimal bigDecimalValue() {
        return getDefaultScaledDelegate().bigDecimalValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return getDefaultScaledDelegate().bigIntegerValue();
    }

    @Override
    public int intValue() {
        return getDefaultScaledDelegate().intValue();
    }

    @Override
    public long longValue() {
        return getDefaultScaledDelegate().longValue();
    }

    @Override
    public float floatValue() {
        return getDefaultScaledDelegate().floatValue();
    }

    @Override
    public double doubleValue() {
        return getDefaultScaledDelegate().doubleValue();
    }

    @Override
    public double doubleValueRaw() {
        return getDefaultScaledDelegate().doubleValueRaw();
    }

    @Override
    public byte byteValue() {
        return getDefaultScaledDelegate().byteValue();
    }

    @Override
    public short shortValue() {
        return getDefaultScaledDelegate().shortValue();
    }

    @Override
    public Dfp dfpValue() {
        return getDefaultScaledDelegate().dfpValue();
    }

    @Override
    protected Object internalRound(final Object value, final int scale, final RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getZero() {
        return null;
    }

    @Override
    protected ADecimalImpl newValueCopy(final Object value, final Object defaultRoundedValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ADecimalImpl getGenericThis() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number numberValue() {
        return getDefaultScaledDelegate().numberValue();
    }

    @Override
    protected String internalToString() {
        throw new UnsupportedOperationException();
    }

}
