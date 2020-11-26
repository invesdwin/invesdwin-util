package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamStdev;

@NotThreadSafe
public class DecimalStreamStdev<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamStdev<Double> standardDeviation = new NumberStreamStdev<>();

    private final E converter;

    public DecimalStreamStdev(final E converter) {
        this.converter = converter;
    }

    /**
     * Warning: normally one will use the sampleStandardDeviation since it is hard to come by a complete set of values
     * representing the distribution of reality
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
     * Warning: normally one will use the sampleVariance since it is hard to come by a complete set of values
     * representing the distribution of reality
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
