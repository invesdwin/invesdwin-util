package de.invesdwin.util.math.decimal.internal.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.dfp.Dfp;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.Shorts;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class DoubleDecimalImpl extends ADoubleDecimalImpl<DoubleDecimalImpl> {

    private static final double FIRST_ABOVE_ZERO = 0.000000001;
    private static final double FIRST_BELOW_ZERO = -0.000000001;
    private static final double ZERO = 0d;
    private static final double ZERO_OBJ = ZERO;
    private static final DoubleDecimalImpl ZERO_IMPL = new DoubleDecimalImpl(ZERO_OBJ).setAlreadyDefaultRounded(true);

    static {
        //ensure rounding performance fix uses correct scale
        final NumberFormat df = new DecimalFormat("0.##########################");
        Assertions.assertThat(Strings.countMatches(df.format(FIRST_ABOVE_ZERO), "0"))
                .isEqualTo(ADecimal.DEFAULT_ROUNDING_SCALE);
        Assertions.assertThat(Strings.countMatches(df.format(FIRST_BELOW_ZERO), "0"))
                .isEqualTo(ADecimal.DEFAULT_ROUNDING_SCALE);
    }

    public DoubleDecimalImpl(final Double value) {
        super(value);
        final double doubleValue = getValue();
        if (Double.isNaN(doubleValue)) {
            throw new IllegalArgumentException("NaN: " + doubleValue);
        }
        if (Double.isInfinite(doubleValue)) {
            throw new IllegalArgumentException("Infinite: " + doubleValue);
        }
    }

    @Override
    public boolean isZero() {
        return internalCompareTo(ZERO_IMPL) == 0;
    }

    @Override
    public boolean isPositive() {
        return internalCompareTo(ZERO_IMPL) >= 0;
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
    protected int internalCompareTo(final ADecimal<?> decimalOther) {
        //improve compare performance by rounding less often
        final DoubleDecimalImpl doubleDecimalOther = (DoubleDecimalImpl) decimalOther.getImpl();
        return internalCompareTo(doubleDecimalOther);
    }

    private int internalCompareTo(final DoubleDecimalImpl doubleDecimalOther) {
        final Double doubleOther = doubleDecimalOther.getValue();
        final Double doubleThis = getValue();
        final double difference = doubleThis - doubleOther;
        if (difference > FIRST_ABOVE_ZERO) {
            return 1;
        } else if (difference < FIRST_BELOW_ZERO) {
            return -1;
        } else if (difference == ZERO) {
            return 0;
        } else {
            final double roundedOther = doubleDecimalOther.getDefaultRoundedValue();
            final double defaultRoundedValue = getDefaultRoundedValue();
            if (defaultRoundedValue < roundedOther) {
                return -1;
            } else if (defaultRoundedValue > roundedOther) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public DoubleDecimalImpl scaleByPowerOfTen(final int n) {
        return multiply(Doubles.pow(10, n));
    }

    @Override
    public Number numberValue() {
        return getDefaultRoundedValue();
    }

    @Override
    public double doubleValue() {
        return getDefaultRoundedValue();
    }

    @Override
    public double doubleValueRaw() {
        return getValue();
    }

    @Override
    public float floatValue() {
        return Floats.checkedCast(getDefaultRoundedValue());
    }

    @Override
    public int intValue() {
        return Integers.checkedCast(getDefaultRoundedValue());
    }

    @Override
    public long longValue() {
        return Longs.checkedCast(getDefaultRoundedValue());
    }

    @Override
    public byte byteValue() {
        return Bytes.checkedCast(getDefaultRoundedValue());
    }

    @Override
    public short shortValue() {
        return Shorts.checkedCast(getDefaultRoundedValue());
    }

    @Override
    public DoubleDecimalImpl abs() {
        return newValueCopy(Doubles.abs(getValue()));
    }

    @Override
    public DoubleDecimalImpl root(final ADecimal<?> n) {
        return root(n.doubleValueRaw());
    }

    @Override
    public DoubleDecimalImpl root(final Number n) {
        return newValueCopy(Doubles.root(getValue(), n.doubleValue()));
    }

    @Override
    public DoubleDecimalImpl sqrt() {
        return newValueCopy(Doubles.sqrt(getValue()));
    }

    @Override
    public DoubleDecimalImpl pow(final Number exponent) {
        final double a = getValue();
        final double b = exponent.doubleValue();
        double pow = Math.pow(a, b);
        if (Double.isNaN(pow) && a < 0D) {
            final double absA = Doubles.abs(a);
            pow = -Math.pow(absA, b);
        }
        return newValueCopy(pow);
    }

    @Override
    public DoubleDecimalImpl pow(final ADecimal<?> exponent) {
        return pow(exponent.doubleValueRaw());
    }

    @Override
    public DoubleDecimalImpl subtract(final ADecimal<?> subtrahend) {
        final double value = getValue() - subtrahend.doubleValueRaw();
        return newValueCopy(value);
    }

    @Override
    public DoubleDecimalImpl add(final ADecimal<?> augend) {
        final double value = getValue() + augend.doubleValueRaw();
        return newValueCopy(value);
    }

    @Override
    public DoubleDecimalImpl multiply(final ADecimal<?> multiplicant) {
        return newValueCopy(getValue() * multiplicant.doubleValueRaw());
    }

    @Override
    public DoubleDecimalImpl multiply(final Number multiplicant) {
        return newValueCopy(getValue() * multiplicant.doubleValue());
    }

    @Override
    public DoubleDecimalImpl divide(final ADecimal<?> divisor) {
        return newValueCopy(getValue() / divisor.doubleValueRaw());
    }

    @Override
    public DoubleDecimalImpl divide(final Number divisor) {
        return newValueCopy(getValue() / divisor.doubleValue());
    }

    @Override
    public DoubleDecimalImpl remainder(final ADecimal<?> divisor) {
        return newValueCopy(Doubles.remainder(getValue(), divisor.doubleValueRaw()));
    }

    @Override
    public DoubleDecimalImpl remainder(final Number divisor) {
        return newValueCopy(Doubles.remainder(getValue(), divisor.doubleValue()));
    }

    @Override
    public DoubleDecimalImpl log() {
        return newValueCopy(Doubles.log(getValue()));
    }

    @Override
    public DoubleDecimalImpl exp() {
        return newValueCopy(Doubles.exp(getValue()));
    }

    @Override
    public DoubleDecimalImpl log10() {
        return newValueCopy(Doubles.log10(getValue()));
    }

    @Override
    public DoubleDecimalImpl exp10() {
        return newValueCopy(Doubles.exp10(getValue()));
    }

    @Override
    public DoubleDecimalImpl cos() {
        return newValueCopy(Doubles.cos(getValue()));
    }

    @Override
    public DoubleDecimalImpl sin() {
        return newValueCopy(Doubles.sin(getValue()));
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
        return DfpDecimalImplFactory.toDfp(getDefaultRoundedValue());
    }

    @Override
    protected double internalRound(final double value, final int scale, final RoundingMode roundingMode) {
        return Doubles.round(value, scale, roundingMode);
    }

    @Override
    protected double getZero() {
        return ZERO;
    }

    @Override
    protected DoubleDecimalImpl newValueCopy(final double value) {
        return new DoubleDecimalImpl(value);
    }

    @Override
    protected DoubleDecimalImpl getGenericThis() {
        return this;
    }

}
