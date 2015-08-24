package de.invesdwin.util.assertions.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.data.Offset;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@SuppressWarnings("rawtypes")
@NotThreadSafe
class Decimals extends org.assertj.core.internal.Numbers {

    public static final Decimals INSTANCE = new Decimals();

    private Decimals() {}

    public Decimals(final org.assertj.core.internal.ComparisonStrategy comparisonStrategy) {
        super(comparisonStrategy);
    }

    @Override
    protected ADecimal<?> zero() {
        return Decimal.ZERO;
    }

    @Override
    public void assertIsCloseTo(final AssertionInfo info, final Number actual, final Number other, final Offset offset) {
        throw new UnsupportedOperationException("TODO");
    }
}
