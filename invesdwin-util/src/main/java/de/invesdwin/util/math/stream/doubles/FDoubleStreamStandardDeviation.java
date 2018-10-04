package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamStandardDeviation;

@NotThreadSafe
public class FDoubleStreamStandardDeviation<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamStandardDeviation<Double> standardDeviation = new NumberStreamStandardDeviation<>();

    private final E converter;

    public FDoubleStreamStandardDeviation(final E converter) {
        this.converter = converter;
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public E getStandardDeviation() {
        final double result = standardDeviation.getStandardDeviation();
        return converter.fromDefaultValue(result);
    }

    public E getSampleStandardDeviation() {
        final double result = standardDeviation.getSampleStandardDeviation();
        return converter.fromDefaultValue(result);
    }

    public E getSampleVariance() {
        final double result = standardDeviation.getSampleVariance();
        return converter.fromDefaultValue(result);
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public E getVariance() {
        final double result = standardDeviation.getVariance();
        return converter.fromDefaultValue(result);
    }

    public E getAvg() {
        final double result = standardDeviation.getAvg();
        return converter.fromDefaultValue(result);
    }

    public long getCount() {
        return standardDeviation.getCount();
    }

    @Override
    public Void process(final E value) {
        standardDeviation.process(value.getDefaultValue());
        return null;
    }

}
