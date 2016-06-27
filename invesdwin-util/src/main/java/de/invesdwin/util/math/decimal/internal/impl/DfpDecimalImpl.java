package de.invesdwin.util.math.decimal.internal.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.dfp.DfpMath;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public class DfpDecimalImpl extends ADecimalImpl<DfpDecimalImpl, Dfp> {

    public DfpDecimalImpl(final Dfp value, final Dfp defaultRoundedValue) {
        super(value, defaultRoundedValue);
    }

    @Override
    public boolean isZero() {
        return getDefaultRoundedValue().isZero();
    }

    @Override
    public boolean isPositive() {
        return !getDefaultRoundedValue().lessThan(getValue().getZero());
    }

    @Override
    public String internalToString() {
        return getDefaultRoundedValue().toString();
    }

    @Override
    protected int internalCompareTo(final ADecimal<?> decimalOther) {
        final Dfp dfpOther = decimalOther.round().dfpValue();
        if (getDefaultRoundedValue().greaterThan(dfpOther)) {
            return 1;
        } else if (getDefaultRoundedValue().lessThan(dfpOther)) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public DfpDecimalImpl abs() {
        return newValueCopy(getValue().abs());
    }

    @Override
    public DfpDecimalImpl scaleByPowerOfTen(final int n) {
        //power10 method somehow messes things up badly, thus using the multiplication instead
        return multiply(Math.pow(10, n));
    }

    @Override
    public DfpDecimalImpl root(final Number n) {
        final Dfp log = DfpMath.log(getValue());
        final Dfp result = DfpMath.exp(log.divide(n.doubleValue()));
        return newValueCopy(result);
    }

    @Override
    public DfpDecimalImpl root(final ADecimal<?> n) {
        return root(n.doubleValue());
    }

    @Override
    public DfpDecimalImpl pow(final Number exponent) {
        return newValueCopy(DfpMath.pow(getValue(), DfpDecimalImplFactory.toDfp(exponent)));
    }

    @Override
    public DfpDecimalImpl pow(final ADecimal<?> exponent) {
        return pow(exponent.doubleValue());
    }

    @Override
    public DfpDecimalImpl subtract(final ADecimal<?> subtrahend) {
        return newValueCopy(getValue().subtract(subtrahend.dfpValue()));
    }

    @Override
    public DfpDecimalImpl add(final ADecimal<?> augend) {
        return newValueCopy(getValue().add(augend.dfpValue()));
    }

    @Override
    public DfpDecimalImpl multiply(final Number multiplicant) {
        return newValueCopy(getValue().multiply(DfpDecimalImplFactory.toDfp(multiplicant)));
    }

    @Override
    public DfpDecimalImpl multiply(final ADecimal<?> multiplicant) {
        return newValueCopy(getValue().multiply(multiplicant.dfpValue()));
    }

    @Override
    public DfpDecimalImpl divide(final Number divisor) {
        return newValueCopy(getValue().divide(DfpDecimalImplFactory.toDfp(divisor)));
    }

    @Override
    public DfpDecimalImpl divide(final ADecimal<?> divisor) {
        return newValueCopy(getValue().divide(divisor.dfpValue()));
    }

    @Override
    public DfpDecimalImpl remainder(final Number divisor) {
        return newValueCopy(getValue().remainder(DfpDecimalImplFactory.toDfp(divisor)));
    }

    @Override
    public DfpDecimalImpl remainder(final ADecimal<?> divisor) {
        return newValueCopy(getValue().remainder(divisor.dfpValue()));
    }

    @Override
    public DfpDecimalImpl sqrt() {
        return newValueCopy(getValue().sqrt());
    }

    @Override
    public Dfp internalRound(final Dfp value, final int scale, final RoundingMode roundingMode) {
        final Dfp factor = DfpDecimalImplFactory.toDfp((long) Math.pow(10, scale));
        final Dfp toBeRoundedValue = value.multiply(factor);
        final Dfp roundedValue;
        switch (roundingMode) {
        case CEILING:
            roundedValue = toBeRoundedValue.ceil();
            break;
        case UP:
            if (!toBeRoundedValue.lessThan(DfpDecimalImplFactory.ZERO)) {
                roundedValue = DfpDecimalImplFactory.toDfp((long) toBeRoundedValue.toDouble() + 1d);
            } else {
                roundedValue = DfpDecimalImplFactory.toDfp((long) toBeRoundedValue.toDouble() - 1d);
            }
            break;
        case FLOOR:
            roundedValue = toBeRoundedValue.floor();
            break;
        case DOWN:
            roundedValue = DfpDecimalImplFactory.toDfp((long) toBeRoundedValue.toDouble());
            break;
        case HALF_DOWN:
            if (!toBeRoundedValue.lessThan(DfpDecimalImplFactory.ZERO)) {
                roundedValue = toBeRoundedValue.subtract(DfpDecimalImplFactory.ZERO_POINT_FIVE).ceil();
            } else {
                roundedValue = toBeRoundedValue.add(DfpDecimalImplFactory.ZERO_POINT_FIVE).floor();
            }
            break;
        case HALF_EVEN:
            roundedValue = toBeRoundedValue.rint();
            break;
        case HALF_UP:
            if (!toBeRoundedValue.lessThan(DfpDecimalImplFactory.ZERO)) {
                roundedValue = toBeRoundedValue.add(DfpDecimalImplFactory.ZERO_POINT_FIVE).floor();
            } else {
                roundedValue = toBeRoundedValue.subtract(DfpDecimalImplFactory.ZERO_POINT_FIVE).ceil();
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(RoundingMode.class, roundingMode);
        }
        return roundedValue.divide(factor);
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(getDefaultRoundedValue().toString());
    }

    @Override
    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public int intValue() {
        return getDefaultRoundedValue().intValue();
    }

    @Override
    public long longValue() {
        return Double.valueOf(getDefaultRoundedValue().toDouble()).longValue();
    }

    @Override
    public float floatValue() {
        return Double.valueOf(getDefaultRoundedValue().toDouble()).floatValue();
    }

    @Override
    public double doubleValue() {
        return getDefaultRoundedValue().toDouble();
    }

    @Override
    public byte byteValue() {
        return Double.valueOf(getDefaultRoundedValue().toDouble()).byteValue();
    }

    @Override
    public short shortValue() {
        return Double.valueOf(getDefaultRoundedValue().toDouble()).shortValue();
    }

    @Override
    public Dfp dfpValue() {
        return getDefaultRoundedValue();
    }

    @Override
    public Number numberValue() {
        return doubleValue();
    }

    @Override
    protected Dfp getZero() {
        return DfpDecimalImplFactory.ZERO;
    }

    @Override
    protected DfpDecimalImpl newValueCopy(final Dfp value, final Dfp defaultRoundedValue) {
        return new DfpDecimalImpl(value, defaultRoundedValue);
    }

    @Override
    protected DfpDecimalImpl getGenericThis() {
        return this;
    }

}
