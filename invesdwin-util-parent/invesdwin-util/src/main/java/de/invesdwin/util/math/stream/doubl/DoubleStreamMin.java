package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamMin implements IDoubleStreamAlgorithm {

    private double min = Doubles.MAX_VALUE;

    @Override
    public double process(final double value) {
        min = Doubles.min(min, value);
        return min;
    }

    public double getMin() {
        if (min == Doubles.MAX_VALUE) {
            return Double.NaN;
        } else {
            return min;
        }
    }

}
