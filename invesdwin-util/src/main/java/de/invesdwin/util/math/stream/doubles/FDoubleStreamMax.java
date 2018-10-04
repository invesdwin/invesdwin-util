package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamMax<E extends AFDouble<E>> implements IStreamAlgorithm<E, E> {

    private E max;

    @Override
    public E process(final E value) {
        max = FDouble.max(max, value);
        return max;
    }

    public E getMax() {
        return max;
    }

}
