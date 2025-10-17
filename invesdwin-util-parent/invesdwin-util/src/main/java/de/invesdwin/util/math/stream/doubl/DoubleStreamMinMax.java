package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamMinMax implements IDoubleStreamAlgorithm {

    private final DoubleStreamMin minDelegate = new DoubleStreamMin();
    private final DoubleStreamMax maxDelegate = new DoubleStreamMax();

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

    public void reset() {
        maxDelegate.reset();
        minDelegate.reset();
    }

}
