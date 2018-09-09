package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class NumberStreamAvg<E extends Number> implements IStreamAlgorithm<E, Double> {

    private long count = 0;
    private double avg = 0D; // our online mean estimate

    @Override
    public Double process(final E value) {
        count++;
        final double doubleValue = value.doubleValue();
        final double delta = doubleValue - avg;
        avg += delta / count;
        return delta;
    }

    public double getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

}
