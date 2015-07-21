package de.invesdwin.util.math.decimal.internal.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class DoubleDecimalImpl extends ADecimalImpl<DoubleDecimalImpl, Double> {

    private static final Double ZERO = 0d;

    public DoubleDecimalImpl(final Double value, final Double defaultRoundedValue) {
        super(value, defaultRoundedValue);
    }

    @Override
    public boolean isZero() {
        return getDefaultRoundedValue().equals(ZERO);
    }

    @Override
    public boolean isPositive() {
        return getDefaultRoundedValue() >= ZERO;
    }

    @Override
    public String internalToString() {
        final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(MathContext.DECIMAL128.getPrecision());
        format.setRoundingMode(Decimal.DEFAULT_ROUNDING_MODE);
        format.setGroupingUsed(false);
        return format.format(getDefaultRoundedValue());
    }

    @Override
    protected int internalCompareTo(final ADecimal<?> defaultRoundedOther) {
        final double doundedOther = defaultRoundedOther.doubleValue();
        return getDefaultRoundedValue().compareTo(doundedOther);
    }

    @Override
    public DoubleDecimalImpl scaleByPowerOfTen(final int n) {
        return multiply(Math.pow(10, n));
    }

    @Override
    public double doubleValue() {
        return getValue().doubleValue();
    }

    @Override
    public float floatValue() {
        return getValue().floatValue();
    }

    @Override
    public int intValue() {
        return getValue().intValue();
    }

    @Override
    public long longValue() {
        return getValue().longValue();
    }

    @Override
    public byte byteValue() {
        return getValue().byteValue();
    }

    @Override
    public short shortValue() {
        return getValue().shortValue();
    }

    @Override
    public DoubleDecimalImpl abs() {
        return newValueCopy(Math.abs(getValue()));
    }

    @Override
    public DoubleDecimalImpl root(final int n) {
        final double log = Math.log(getValue());
        final double result = Math.exp(log / n);
        return newValueCopy(result);
    }

    @Override
    public DoubleDecimalImpl sqrt() {
        return newValueCopy(Math.sqrt(getValue()));
    }

    @Override
    public DoubleDecimalImpl pow(final int exponent) {
        return newValueCopy(Math.pow(getValue(), exponent));
    }

    @Override
    public DoubleDecimalImpl subtract(final ADecimal<?> subtrahend) {
        final double value = getValue() - subtrahend.doubleValue();
        return newValueCopy(value, value);
    }

    @Override
    public DoubleDecimalImpl add(final ADecimal<?> augend) {
        final double value = getValue() + augend.doubleValue();
        return newValueCopy(value, value);
    }

    @Override
    public DoubleDecimalImpl multiply(final ADecimal<?> multiplicant) {
        return newValueCopy(getValue() * multiplicant.doubleValue());
    }

    @Override
    public DoubleDecimalImpl multiply(final Number multiplicant) {
        return newValueCopy(getValue() * multiplicant.doubleValue());
    }

    @Override
    public DoubleDecimalImpl divide(final ADecimal<?> divisor) {
        return newValueCopy(getValue() / divisor.doubleValue());
    }

    @Override
    public DoubleDecimalImpl divide(final Number divisor) {
        return newValueCopy(getValue() / divisor.doubleValue());
    }

    @Override
    public DoubleDecimalImpl remainder(final ADecimal<?> divisor) {
        return newValueCopy(getValue() % divisor.doubleValue());
    }

    @Override
    public DoubleDecimalImpl remainder(final Number divisor) {
        return newValueCopy(getValue() % divisor.doubleValue());
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return BigDecimalDecimalImplFactory.toBigDecimal(getValue());
    }

    @Override
    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public Dfp dfpValue() {
        return DfpDecimalImplFactory.toDfp(getValue());
    }

    @Override
    protected Double internalRound(final Double value, final int scale, final RoundingMode roundingMode) {
        final long factor = (long) Math.pow(10, scale);
        final double toBeRoundedValue;
        if (scale < Decimal.DEFAULT_ROUNDING_SCALE && roundingMode != Decimal.DEFAULT_ROUNDING_MODE) {
            //fix 1 represented as 0.9999999 becoming 0 here instead of correctly being 1; for instance in FLOOR rounding mode
            toBeRoundedValue = internalRound(value, scale + Decimal.DEFAULT_ROUNDING_SCALE,
                    Decimal.DEFAULT_ROUNDING_MODE) * factor;
        } else {
            toBeRoundedValue = value * factor;
        }

        final double roundedValue;
        switch (roundingMode) {
        case CEILING:
            roundedValue = Math.ceil(toBeRoundedValue);
            break;
        case UP:
            if (toBeRoundedValue >= 0) {
                roundedValue = (long) (toBeRoundedValue + 1d);
            } else {
                roundedValue = (long) (toBeRoundedValue - 1d);
            }
            break;
        case FLOOR:
            roundedValue = Math.floor(toBeRoundedValue);
            break;
        case DOWN:
            roundedValue = (long) toBeRoundedValue;
            break;
        case HALF_DOWN:
            if (toBeRoundedValue >= 0) {
                roundedValue = Math.ceil(toBeRoundedValue - 0.5d);
            } else {
                roundedValue = Math.floor(toBeRoundedValue + 0.5d);
            }
            break;
        case HALF_EVEN:
            //if the value is even and the fraction is 0.5, we need to round to the even number
            final long longValue = (long) toBeRoundedValue;
            if (longValue % 2 == 0) {
                //need to rounded here, since 0.5 can not be represented properly for doubles
                final long firstFractionalDigit = Math.abs(Math.round(toBeRoundedValue % 1 * 10));
                if (firstFractionalDigit == 5) {
                    roundedValue = longValue;
                    break;
                }
            }

            //otherwise round to the nearest number
            roundedValue = Math.rint(toBeRoundedValue);
            break;
        case HALF_UP:
            if (toBeRoundedValue >= 0) {
                roundedValue = Math.floor(toBeRoundedValue + 0.5d);
            } else {
                roundedValue = Math.ceil(toBeRoundedValue - 0.5);
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(RoundingMode.class, roundingMode);
        }
        return roundedValue / factor;
    }

    @Override
    protected Double getZero() {
        return ZERO;
    }

    @Override
    protected DoubleDecimalImpl newValueCopy(final Double value, final Double defaultRoundedValue) {
        return new DoubleDecimalImpl(value, defaultRoundedValue);
    }

    @Override
    protected DoubleDecimalImpl getGenericThis() {
        return this;
    }

    @Override
    public Number numberValue() {
        return getValue();
    }

}
