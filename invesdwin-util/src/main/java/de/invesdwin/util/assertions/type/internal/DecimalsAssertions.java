package de.invesdwin.util.assertions.type.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.data.Offset;

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
    public void assertIsCloseTo(final AssertionInfo info, final Number actual, final Number other,
            final Offset offset) {
        throw new UnsupportedOperationException("TODO");
    }
}
