package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamSum implements IDoubleStreamAlgorithm {

    private double sum = 0D;

    @Override
    public double process(final double value) {
        sum += value;
        return sum;
    }

    public double getSum() {
        return sum;
    }

    public void reset() {
        sum = 0D;
    }

}
