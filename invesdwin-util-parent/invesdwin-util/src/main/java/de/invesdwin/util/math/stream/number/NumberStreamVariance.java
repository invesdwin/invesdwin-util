package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class NumberStreamVariance<E extends Number> implements IStreamAlgorithm<E, Void> {

    private double squareSum = 0.0;
    private final NumberStreamAvg<Double> avg = new NumberStreamAvg<>();

    public double getSampleVariance() {
        final long count = avg.getCount();
        if (count < 2) {
            return 0D;
        } else {
            final double result = squareSum / (count - 1); // sample variance N-1
            return result;
        }
    }

    /**
     * Warning: normally one will use the sampleCoefficientOfVariation since it is hard to come by a complete set of
     * values representing the distribution of reality
     */
    @Deprecated
    public double getVariance() {
        final long count = avg.getCount();
        if (count < 2) {
            return 0D;
        } else {
            final double result = squareSum / (count); // variance N
            return result;
        }
    }

    public double getAvg() {
        return avg.getAvg();
    }

    public long getCount() {
        return avg.getCount();
    }

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValue();
        final double delta = avg.process(doubleValue);
        squareSum += delta * (doubleValue - avg.getAvg());
        return null;
    }

}
