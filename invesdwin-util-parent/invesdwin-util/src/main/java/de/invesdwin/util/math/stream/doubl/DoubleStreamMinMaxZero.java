package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamMinMaxZero implements IDoubleStreamAlgorithm {

    private final DoubleStreamMinZero minDelegate = new DoubleStreamMinZero();
    private final DoubleStreamMaxZero maxDelegate = new DoubleStreamMaxZero();

    @Override
    public double process(final double value) {
        minDelegate.process(value);
        maxDelegate.process(value);
        return Double.NaN;
    }

    public double getMin() {
        return minDelegate.getMin();
    }

    public double getMax() {
        return maxDelegate.getMax();
    }

}
