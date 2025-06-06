package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamMax implements IDoubleStreamAlgorithm {

    private double max = Double.NaN;

    @Override
    public double process(final double value) {
        max = Doubles.max(max, value);
        return max;
    }

    public double getMax() {
        return max;
    }

    public void reset() {
        max = Double.NaN;
    }

}
