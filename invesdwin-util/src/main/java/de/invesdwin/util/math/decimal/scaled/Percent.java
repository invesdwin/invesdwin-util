package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@SuppressWarnings("serial")
@Immutable
public class Percent extends AScaledDecimal<Percent, PercentScale> implements IPercentData {

    public static final PercentScale DEFAULT_SCALE;
    public static final Percent THREE_HUNDRED_PERCENT;
    public static final Percent TWO_HUNDRED_PERCENT;
    public static final Percent ONE_HUNDRED_PERCENT;
    public static final Percent FIFTY_PERCENT;
    public static final Percent ZERO_PERCENT;
    public static final Percent ONE_PERCENT;
    public static final Percent MINUS_ONE_PERCENT;

    static {
        DEFAULT_SCALE = PercentScale.RATE;
        THREE_HUNDRED_PERCENT = new Percent(Decimal.THREE, PercentScale.RATE);
        TWO_HUNDRED_PERCENT = new Percent(Decimal.TWO, PercentScale.RATE);
        ONE_HUNDRED_PERCENT = new Percent(Decimal.ONE, PercentScale.RATE);
        FIFTY_PERCENT = new Percent(new Decimal("50"), PercentScale.PERCENT);
        ZERO_PERCENT = new Percent(Decimal.ZERO, PercentScale.RATE);
        ONE_PERCENT = new Percent(Decimal.ONE, PercentScale.PERCENT);
        MINUS_ONE_PERCENT = ONE_PERCENT.negate();
    }

    public Percent(final Decimal value, final PercentScale scale) {
        super(value, scale, DEFAULT_SCALE);
    }

    public Percent(final Number dividend, final Number divisor) {
        this(Decimal.valueOf(dividend), Decimal.valueOf(divisor));
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public Percent(final AScaledDecimal<?, ?> dividend, final Number divisor) throws Exception {
        super(Decimal.ZERO, PercentScale.PERCENT, DEFAULT_SCALE);
        throw new UnsupportedOperationException();
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public Percent(final Number dividend, final AScaledDecimal<?, ?> divisor) throws Exception {
        super(null, null, DEFAULT_SCALE);
        throw new UnsupportedOperationException();
    }

    public Percent(final ADecimal<?> dividend, final ADecimal<?> divisor) {
        this(dividend.getDefaultValue().divide(divisor.getDefaultValue()), PercentScale.RATE);
    }

    public Percent(final Duration dividend, final Duration divisor) {
        this(dividend.doubleValue(FTimeUnit.MILLISECONDS), divisor.doubleValue(FTimeUnit.MILLISECONDS));
    }

    public <T extends AScaledDecimal<T, ?>> Percent(final AScaledDecimal<T, ?> dividend,
            final AScaledDecimal<T, ?> divisor) {
        this(dividend.getDefaultValue().divide(divisor.getDefaultValue()), PercentScale.RATE);
    }

    public Percent(final Percent percent) {
        this(percent.getValue(percent.getScale()), percent.getScale());
    }

    @Override
    protected Percent getGenericThis() {
        return this;
    }

    @Override
    protected Percent newValueCopy(final Decimal value, final PercentScale scale) {
        return new Percent(value, scale);
    }

    @Override
    public Percent zero() {
        return ZERO_PERCENT;
    }

    public static Percent nullToZero(final Percent value) {
        if (value == null) {
            return ZERO_PERCENT;
        } else {
            return value;
        }
    }

    @Override
    public Decimal getRate() {
        return getDefaultValue();
    }

    /**
     * (newValue - oldValue) / abs(oldValue)
     */
    public static <T extends ADecimal<T>> Percent relativeDifference(final ADecimal<T> oldValue,
            final ADecimal<T> newValue) {
        return new Percent(newValue.subtract(oldValue), oldValue.abs());
    }

}
