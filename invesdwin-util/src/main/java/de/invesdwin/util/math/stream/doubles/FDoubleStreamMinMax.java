package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamMinMax<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private final FDoubleStreamMin<E> minDelegate = new FDoubleStreamMin<E>();;
    private final FDoubleStreamMax<E> maxDelegate = new FDoubleStreamMax<E>();

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
