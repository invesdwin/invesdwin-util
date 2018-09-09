package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamVariance;

@NotThreadSafe
public class DecimalStreamVariance<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamVariance<Double> variance = new NumberStreamVariance<>();

    private final E converter;

    public DecimalStreamVariance(final E converter) {
        this.converter = converter;
    }

    public E getSampleVariance() {
        final double result = variance.getSampleVariance();
        return converter.fromDefaultValue(new Decimal(result));
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public E getVariance() {
        final double result = variance.getVariance();
        return converter.fromDefaultValue(new Decimal(result));
    }

    public E getAvg() {
        final double result = variance.getAvg();
        return converter.fromDefaultValue(new Decimal(result));
    }

    public long getCount() {
        return variance.getCount();
    }

    @Override
    public Void process(final E value) {
        variance.process(value.getDefaultValue().doubleValueRaw());
        return null;
    }

}
