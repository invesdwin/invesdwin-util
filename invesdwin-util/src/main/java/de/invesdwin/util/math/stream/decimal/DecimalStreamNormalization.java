package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamNormalization<E extends ADecimal<E>> implements IStreamAlgorithm<E, E> {

    private final E min;
    private final E max;
    private final E maxMinusMin;

    public DecimalStreamNormalization(final E min, final E max) {
        Assertions.assertThat(min).isLessThanOrEqualTo(max);
        this.min = min;
        this.max = max;
        this.maxMinusMin = max.subtract(min);
    }

    public E getMin() {
        return min;
    }

    public E getMax() {
        return max;
    }

    /**
     * normalized(x) = (x-min(x))/(max(x)-min(x))
     */
    @Override
    public E process(final E value) {
        return value.subtract(min).divide(maxMinusMin);
    }

}
