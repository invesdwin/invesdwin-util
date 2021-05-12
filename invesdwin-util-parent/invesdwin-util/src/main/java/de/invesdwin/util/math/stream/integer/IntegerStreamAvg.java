package de.invesdwin.util.math.stream.integer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class IntegerStreamAvg implements IIntegerStreamAlgorithm {

    private long count = 0;
    private double avg = 0D; // our online mean estimate

    @Override
    public int process(final int value) {
        count++;
        final double delta = value - avg;
        avg += delta / count;
        return (int) delta;
    }

    public int getAvg() {
        return (int) avg;
    }

    public long getCount() {
        return count;
    }

    public void reset() {
        count = 0;
        avg = 0D;
    }

}
