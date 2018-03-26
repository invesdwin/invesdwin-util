package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamMinMax<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private final DecimalStreamMin<E> minDelegate = new DecimalStreamMin<E>();;
    private final DecimalStreamMax<E> maxDelegate = new DecimalStreamMax<E>();

    @Override
    public Void process(final E value) {
        minDelegate.process(value);
        maxDelegate.process(value);
        return null;
    }

    public E getMin() {
        return minDelegate.getMin();
    }

    public E getMax() {
        return maxDelegate.getMax();
    }

}
