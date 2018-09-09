package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class NumberStreamStandardDeviation<E extends Number> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamVariance<Double> variance = new NumberStreamVariance<>();

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public double getStandardDeviation() {
        return Math.sqrt(variance.getVariance());
    }

    public double getSampleStandardDeviation() {
        return Math.sqrt(variance.getSampleVariance());
    }

    public double getSampleVariance() {
        return variance.getSampleVariance();
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public double getVariance() {
        return variance.getVariance();
    }

    public double getAvg() {
        return variance.getAvg();
    }

    public long getCount() {
        return variance.getCount();
    }

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValue();
        variance.process(doubleValue);
        return null;
    }

}
