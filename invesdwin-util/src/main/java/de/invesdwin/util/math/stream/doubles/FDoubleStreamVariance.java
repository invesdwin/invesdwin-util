package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamVariance;

@NotThreadSafe
public class FDoubleStreamVariance<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamVariance<Double> variance = new NumberStreamVariance<>();

    private final E converter;

    public FDoubleStreamVariance(final E converter) {
        this.converter = converter;
    }

    public E getSampleVariance() {
        final double result = variance.getSampleVariance();
        return converter.fromDefaultValue(result);
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public E getVariance() {
        final double result = variance.getVariance();
        return converter.fromDefaultValue(result);
    }

    public E getAvg() {
        final double result = variance.getAvg();
        return converter.fromDefaultValue(result);
    }

    public long getCount() {
        return variance.getCount();
    }

    @Override
    public Void process(final E value) {
        variance.process(value.getDefaultValue());
        return null;
    }

}
