package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamStandardDeviation implements IDoubleStreamAlgorithm {

    private final DoubleStreamVariance variance = new DoubleStreamVariance();

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
    public double process(final double value) {
        variance.process(value);
        return Double.NaN;
    }

}
