package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamMin<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, E> {

    private E min;

    @Override
    public E process(final E value) {
        min = Decimal.min(min, value);
        return min;
    }

    public E getMin() {
        return min;
    }
}
