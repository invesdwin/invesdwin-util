package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamMax<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, E> {

    private E max;

    @Override
    public E process(final E value) {
        max = Decimal.max(max, value);
        return max;
    }

    public E getMax() {
        return max;
    }

}
