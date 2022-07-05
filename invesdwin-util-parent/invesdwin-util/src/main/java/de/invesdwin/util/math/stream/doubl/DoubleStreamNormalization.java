package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;

@NotThreadSafe
public class DoubleStreamNormalization<E extends ADecimal<E>> implements IDoubleStreamAlgorithm {

    private final double min;
    private final double max;
    private final double maxMinusMin;

    public DoubleStreamNormalization(final double min, final double max) {
        Assertions.assertThat(min).isLessThanOrEqualTo(max);
        this.min = min;
        this.max = max;
        this.maxMinusMin = max - min;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    /**
     * normalized(x) = (x-min(x))/(max(x)-min(x))
     */
    @Override
    public double process(final double value) {
        return Doubles.divide(value - min, maxMinusMin);
    }

}
