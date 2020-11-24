package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamMinZero implements IDoubleStreamAlgorithm {

    private double min = Double.NaN;

    @Override
    public double process(final double value) {
        min = Doubles.min(min, value);
        return min;
    }

    public double getMin() {
        if (Doubles.isNaN(min)) {
            return 0D;
        } else {
            return min;
        }
    }

}
