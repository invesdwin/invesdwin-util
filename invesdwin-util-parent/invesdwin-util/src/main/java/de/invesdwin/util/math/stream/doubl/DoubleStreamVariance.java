package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamVariance implements IDoubleStreamAlgorithm {

    private double squareSum = 0.0;
    private final DoubleStreamAvg avg = new DoubleStreamAvg();

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
    public double process(final double value) {
        final double delta = avg.process(value);
        squareSum += delta * (value - avg.getAvg());
        return Double.NaN;
    }

}
