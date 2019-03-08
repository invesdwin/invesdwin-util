package de.invesdwin.util.math.stream.number;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
class OverflowingNumberStreamVariance<E extends Number> implements IStreamAlgorithm<E, Void> {

    private final OverflowingNumberStreamAvg<Double> avgProcessor = new OverflowingNumberStreamAvg<>();
    private final List<Double> values = new ArrayList<>();

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValue();
        avgProcessor.process(doubleValue);
        values.add(doubleValue);
        return null;
    }

    /**
     * s^2 = 1/(n) * sum((x_i - x_quer)^2)
     * 
     * <a href="http://de.wikipedia.org/wiki/Stichprobenvarianz">Source</a>
     */
    public double getVariance() {
        final double avg = avgProcessor.getAvg();
        double sum = 0D;
        for (final double value : values) {
            sum += Math.pow(value - avg, 2);
        }
        return sum / (values.size());
    }

    /**
     * s^2 = 1/(n-1) * sum((x_i - x_quer)^2)
     */
    public double getSampleVariance() {
        final double avg = avgProcessor.getAvg();
        double sum = 0D;
        for (final double value : values) {
            sum += Math.pow(value - avg, 2);
        }
        return sum / (values.size() - 1);
    }

}