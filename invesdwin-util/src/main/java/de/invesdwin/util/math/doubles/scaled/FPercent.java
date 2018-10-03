package de.invesdwin.util.math.doubles.scaled;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.IScaledNumber;
import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.AScaledFDouble;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public class FPercent extends AScaledFDouble<FPercent, FPercentScale> {

    public static final FPercentScale DEFAULT_SCALE;
    public static final FPercent THREE_HUNDRED_PERCENT;
    public static final FPercent TWO_HUNDRED_PERCENT;
    public static final FPercent ONE_HUNDRED_PERCENT;
    public static final FPercent NINETY_PERCENT;
    public static final FPercent SEVENTYFIVE_PERCENT;
    public static final FPercent FIFTY_PERCENT;
    public static final FPercent TWENTYFIVE_PERCENT;
    public static final FPercent TEN_PERCENT;
    public static final FPercent FIVE_PERCENT;
    public static final FPercent TWO_PERCENT;
    public static final FPercent ONE_PERCENT;
    public static final FPercent ZERO_PERCENT;
    public static final FPercent MINUS_ONE_PERCENT;
    public static final FPercent MINUS_TWO_PERCENT;
    public static final FPercent MINUS_FIVE_PERCENT;
    public static final FPercent MINUS_TEN_PERCENT;

    static {
        DEFAULT_SCALE = FPercentScale.RATE;
        THREE_HUNDRED_PERCENT = new FPercent(3D, FPercentScale.RATE);
        TWO_HUNDRED_PERCENT = new FPercent(2D, FPercentScale.RATE);
        ONE_HUNDRED_PERCENT = new FPercent(1D, FPercentScale.RATE);
        NINETY_PERCENT = new FPercent(90D, FPercentScale.PERCENT);
        SEVENTYFIVE_PERCENT = new FPercent(75D, FPercentScale.PERCENT);
        FIFTY_PERCENT = new FPercent(50D, FPercentScale.PERCENT);
        TWENTYFIVE_PERCENT = new FPercent(25D, FPercentScale.PERCENT);
        TEN_PERCENT = new FPercent(10D, FPercentScale.PERCENT);
        FIVE_PERCENT = new FPercent(5D, FPercentScale.PERCENT);
        TWO_PERCENT = new FPercent(2D, FPercentScale.PERCENT);
        ONE_PERCENT = new FPercent(1D, FPercentScale.PERCENT);
        ZERO_PERCENT = new FPercent(0D, FPercentScale.RATE);
        MINUS_ONE_PERCENT = ONE_PERCENT.negate();
        MINUS_TWO_PERCENT = TWO_PERCENT.negate();
        MINUS_FIVE_PERCENT = FIVE_PERCENT.negate();
        MINUS_TEN_PERCENT = TEN_PERCENT.negate();
    }

    public FPercent(final double value, final FPercentScale scale) {
        super(value, scale);
    }

    public FPercent(final Number dividend, final Number divisor) {
        this(dividend.doubleValue(), divisor.doubleValue());
    }

    public FPercent(final Double dividend, final Double divisor) {
        this(dividend.doubleValue(), divisor.doubleValue());
    }

    public FPercent(final double dividend, final double divisor) {
        this(Doubles.divideHandlingZero(dividend, divisor), FPercentScale.RATE);
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public FPercent(final IScaledNumber dividend, final Number divisor) throws Exception {
        super(0D, FPercentScale.PERCENT);
        throw new UnsupportedOperationException();
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public FPercent(final Number dividend, final IScaledNumber divisor) throws Exception {
        super(0D, FPercentScale.PERCENT);
        throw new UnsupportedOperationException();
    }

    public FPercent(final AFDouble<?> dividend, final AFDouble<?> divisor) {
        this(Doubles.divideHandlingZero(dividend.getDefaultValue(), divisor.getDefaultValue()), FPercentScale.RATE);
        if (dividend instanceof AScaledFDouble) {
            final AScaledFDouble<?, ?> cDividend = (AScaledFDouble<?, ?>) dividend;
            cDividend.assertSameDefaultScale(divisor);
        }
    }

    public FPercent(final ADecimal<?> dividend, final ADecimal<?> divisor) {
        this(Doubles.divideHandlingZero(dividend.getDefaultValue().doubleValueRaw(),
                divisor.getDefaultValue().doubleValueRaw()), FPercentScale.RATE);
        if (dividend instanceof AScaledDecimal) {
            final AScaledDecimal<?, ?> cDividend = (AScaledDecimal<?, ?>) dividend;
            cDividend.assertSameDefaultScale(divisor);
        }
    }

    public FPercent(final Duration dividend, final Duration divisor) {
        this(dividend.doubleValue(FTimeUnit.MILLISECONDS), divisor.doubleValue(FTimeUnit.MILLISECONDS));
    }

    public <T extends AScaledFDouble<T, ?>> FPercent(final AScaledFDouble<T, ?> dividend,
            final AScaledFDouble<T, ?> divisor) {
        this(Doubles.divideHandlingZero(dividend.getDefaultValue(), divisor.getDefaultValue()), FPercentScale.RATE);
        dividend.assertSameDefaultScale(divisor);
    }

    public <T extends AScaledDecimal<T, ?>> FPercent(final AScaledDecimal<T, ?> dividend,
            final AScaledDecimal<T, ?> divisor) {
        this(Doubles.divideHandlingZero(dividend.getDefaultValue().doubleValueRaw(),
                divisor.getDefaultValue().doubleValueRaw()), FPercentScale.RATE);
        dividend.assertSameDefaultScale(divisor);
    }

    public FPercent(final FPercent percent) {
        this(percent.getValue(percent.getScale()), percent.getScale());
    }

    @Override
    public FPercentScale getDefaultScale() {
        return DEFAULT_SCALE;
    }

    @Override
    protected FPercent getGenericThis() {
        return this;
    }

    @Override
    protected FPercent newValueCopy(final double value, final FPercentScale scale) {
        return new FPercent(value, scale);
    }

    @Override
    public FPercent zero() {
        return ZERO_PERCENT;
    }

    public static FPercent nullToZero(final FPercent value) {
        if (value == null) {
            return ZERO_PERCENT;
        } else {
            return value;
        }
    }

    public double getRate() {
        return getDefaultValue();
    }

    /**
     * (newValue - oldValue) / abs(oldValue)
     */
    public static <T extends ADecimal<T>> FPercent relativeDifference(final ADecimal<T> oldValue,
            final ADecimal<T> newValue) {
        return new FPercent(newValue.subtract(oldValue), oldValue.abs());
    }

    public static void putPercent(final ByteBuffer buffer, final FPercent value) {
        if (value == null) {
            buffer.putDouble(Double.MIN_VALUE);
        } else {
            buffer.putDouble(value.getRate());
        }
    }

    public static FPercent extractPercent(final ByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractPercent(value);
    }

    public static FPercent extractPercent(final ByteBuffer buffer) {
        final double value = buffer.getDouble();
        return extractPercent(value);
    }

    public static FPercent extractPercent(final double value) {
        if (value == Double.MIN_VALUE) {
            return null;
        } else {
            return new FPercent(value, FPercentScale.RATE);
        }
    }

}
