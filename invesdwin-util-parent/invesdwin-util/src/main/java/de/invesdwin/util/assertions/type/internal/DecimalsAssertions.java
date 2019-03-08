package de.invesdwin.util.assertions.type.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@SuppressWarnings("rawtypes")
@NotThreadSafe
public class DecimalsAssertions extends org.assertj.core.internal.Numbers {

    public static final DecimalsAssertions INSTANCE = new DecimalsAssertions();

    private DecimalsAssertions() {}

    public DecimalsAssertions(final org.assertj.core.internal.ComparisonStrategy comparisonStrategy) {
        super(comparisonStrategy);
    }

    @Override
    protected ADecimal<?> zero() {
        return Decimal.ZERO;
    }

    @Override
    protected ADecimal<?> one() {
        return Decimal.ONE;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ADecimal<?> absDiff(final Number actual, final Number other) {
        return ((ADecimal) actual).subtract((ADecimal) other).abs();
    }

    @Override
    protected boolean isGreaterThan(final Number value, final Number other) {
        return ((ADecimal) value).isGreaterThan(value);
    }
}
