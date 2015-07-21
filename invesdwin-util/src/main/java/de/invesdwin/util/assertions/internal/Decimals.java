package de.invesdwin.util.assertions.internal;

import javax.annotation.concurrent.NotThreadSafe;

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
}
