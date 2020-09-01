package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamMax implements IDoubleStreamAlgorithm {

    private double max = Doubles.MIN_VALUE;

    @Override
    public double process(final double value) {
        max = Doubles.max(max, value);
        return max;
    }

    public double getMax() {
        if (max == Doubles.MIN_VALUE) {
            return Double.NaN;
        } else {
            return max;
        }
    }

}
