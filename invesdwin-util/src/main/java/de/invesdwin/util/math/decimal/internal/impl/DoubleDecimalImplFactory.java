package de.invesdwin.util.math.decimal.internal.impl;

import javax.annotation.concurrent.Immutable;

/**
 * Faster than BigDecimal and ALMOST as accurate since it is rounded after each operation inside implementation. Using
 * double directly would result in much more rounding errors.
 * 
 */
@Immutable
public class DoubleDecimalImplFactory implements IDecimalImplFactory<DoubleDecimalImpl> {

    @Override
    public DoubleDecimalImpl valueOf(final Number value) {
        final double doubleValue = value.doubleValue();
        return new DoubleDecimalImpl(doubleValue, doubleValue);
    }

    @Override
    public DoubleDecimalImpl valueOf(final Double value) {
        return new DoubleDecimalImpl(value, value);
    }

    @Override
    public DoubleDecimalImpl valueOf(final String value) {
        final Double valueOf = Double.valueOf(value);
        return new DoubleDecimalImpl(valueOf, valueOf);
    }

}
