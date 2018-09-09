package de.invesdwin.util.math.stream.number;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
class OverflowingNumberStreamStandardDeviation<E extends Number> implements IStreamAlgorithm<E, Void> {

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
     * s = (1/(n-1) * sum((x_i - x_quer)^2))^1/2
     */
    public double getSampleStandardDeviation() {
        double sum = 0D;
        final double avgDouble = avgProcessor.getAvg();
        for (final double value : values) {
            final double difference = value - avgDouble;
            sum += Math.pow(difference, 2);
        }
        final double divisor = values.size() - 1;
        final double sqrt = Math.sqrt(Doubles.divideHandlingZero(sum, divisor));
        return sqrt;
    }

    /**
     * s = (1/(n) * sum((x_i - x_quer)^2))^1/2
     */
    public double getStandardDeviation() {
        double sum = 0D;
        final double avgDouble = avgProcessor.getAvg();
        for (final double value : values) {
            final double difference = value - avgDouble;
            sum += Math.pow(difference, 2);
        }
        final double divisor = values.size();
        final double sqrt = Math.sqrt(Doubles.divideHandlingZero(sum, divisor));
        return sqrt;
    }

}