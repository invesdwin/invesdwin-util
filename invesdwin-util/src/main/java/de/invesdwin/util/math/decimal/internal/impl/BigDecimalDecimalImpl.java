package de.invesdwin.util.math.decimal.internal.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.util.math.BigDecimals;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class BigDecimalDecimalImpl extends AGenericDecimalImpl<BigDecimalDecimalImpl, BigDecimal> {

    public BigDecimalDecimalImpl(final BigDecimal value, final BigDecimal defaultRoundedValue) {
        super(value, defaultRoundedValue);
    }

    @Override
    public boolean isZero() {
        return getDefaultRoundedValue().compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public boolean isPositive() {
        return getDefaultRoundedValue().compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public String internalToString() {
        return getDefaultRoundedValue().toPlainString();
    }

    @Override
    protected int internalCompareTo(final ADecimal<?> decimalOther) {
        return getDefaultRoundedValue().compareTo(BigDecimalDecimalImplFactory.toBigDecimal(decimalOther.round()));
    }

    @Override
    public int intValue() {
        return getDefaultRoundedValue().intValue();
    }

    @Override
    public long longValue() {
        return getDefaultRoundedValue().longValue();
    }

    @Override
    public float floatValue() {
        return getDefaultRoundedValue().floatValue();
    }

    @Override
    public double doubleValue() {
        return getDefaultRoundedValue().doubleValue();
    }

    @Override
    public double doubleValueRaw() {
        return getValue().doubleValue();
    }

    @Override
    public byte byteValue() {
        return getDefaultRoundedValue().byteValue();
    }

    @Override
    public short shortValue() {
        return getDefaultRoundedValue().shortValue();
    }

    @Override
    public BigDecimalDecimalImpl abs() {
        return newValueCopy(getValue().abs());
    }

    @Override
    public BigDecimalDecimalImpl scaleByPowerOfTen(final int n) {
        return newValueCopy(getValue().scaleByPowerOfTen(n));
    }

    @Override
    public BigDecimalDecimalImpl root(final Number n) {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).root(n).bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl root(final ADecimal<?> n) {
        return null;
    }

    @Override
    public BigDecimalDecimalImpl sqrt() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).sqrt().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl log() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).log().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl exp() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).exp().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl log10() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).log10().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl exp10() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).exp10().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl cos() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).cos().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl sin() {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).sin().bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl pow(final Number exponent) {
        final double doubleValue = getValue().doubleValue();
        return newValueCopy(new DoubleDecimalImpl(doubleValue, doubleValue).pow(exponent).bigDecimalValue());
    }

    @Override
    public BigDecimalDecimalImpl pow(final ADecimal<?> exponent) {
        return pow(exponent.doubleValueRaw());
    }

    @Override
    public BigDecimalDecimalImpl subtract(final ADecimal<?> subtrahend) {
        return newValueCopy(getValue().subtract(subtrahend.bigDecimalValue(), BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl add(final ADecimal<?> augend) {
        return newValueCopy(getValue().add(augend.bigDecimalValue(), BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl multiply(final Number multiplicant) {
        return (BigDecimalDecimalImpl) newValueCopy(getValue()
                .multiply(BigDecimalDecimalImplFactory.toBigDecimal(multiplicant), BigDecimals.DEFAULT_MATH_CONTEXT))
                        .round(Decimal.DEFAULT_ROUNDING_SCALE, Decimal.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public BigDecimalDecimalImpl multiply(final ADecimal<?> multiplicant) {
        return newValueCopy(getValue().multiply(multiplicant.bigDecimalValue(), BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl divide(final Number divisor) {
        return newValueCopy(getValue().divide(BigDecimalDecimalImplFactory.toBigDecimal(divisor),
                BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl divide(final ADecimal<?> divisor) {
        return newValueCopy(getValue().divide(divisor.bigDecimalValue(), BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl remainder(final Number divisor) {
        return newValueCopy(getValue().remainder(BigDecimalDecimalImplFactory.toBigDecimal(divisor),
                BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimalDecimalImpl remainder(final ADecimal<?> divisor) {
        return newValueCopy(getValue().remainder(divisor.bigDecimalValue(), BigDecimals.DEFAULT_MATH_CONTEXT));
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return getDefaultRoundedValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return getDefaultRoundedValue().toBigInteger();
    }

    @Override
    protected BigDecimal internalRound(final BigDecimal value, final int scale, final RoundingMode roundingMode) {
        return value.setScale(scale, roundingMode);
    }

    @Override
    public Dfp dfpValue() {
        return DfpDecimalImplFactory.toDfp(getDefaultRoundedValue());
    }

    @Override
    public Number numberValue() {
        return getDefaultRoundedValue();
    }

    @Override
    protected BigDecimal getZero() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimalDecimalImpl newValueCopy(final BigDecimal value, final BigDecimal defaultRoundedValue) {
        return new BigDecimalDecimalImpl(value, defaultRoundedValue);
    }

    @Override
    protected BigDecimalDecimalImpl getGenericThis() {
        return this;
    }

}
