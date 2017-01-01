package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;

@NotThreadSafe
public class DecimalStreamMinMax<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Void> {

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
