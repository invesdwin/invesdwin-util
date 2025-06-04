package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamMin implements IDoubleStreamAlgorithm {

    private double min = Double.NaN;

    @Override
    public double process(final double value) {
        min = Doubles.min(min, value);
        return min;
    }

    public double getMin() {
        return min;
    }

    public void reset() {
        min = Double.NaN;
    }

}
