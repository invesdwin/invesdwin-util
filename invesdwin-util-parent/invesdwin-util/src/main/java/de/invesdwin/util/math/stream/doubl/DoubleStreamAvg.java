package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamAvg implements IDoubleStreamAlgorithm {

    private long count = 0;
    private double avg = 0D; // our online mean estimate

    @Override
    public double process(final double value) {
        count++;
        final double delta = value - avg;
        avg += delta / count;
        return delta;
    }

    public double getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

    public void reset() {
        count = 0;
        avg = 0D;
    }

}
