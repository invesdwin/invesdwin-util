package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamMin<E extends AFDouble<E>> implements IStreamAlgorithm<E, E> {

    private E min;

    @Override
    public E process(final E value) {
        min = FDouble.min(min, value);
        return min;
    }

    public E getMin() {
        return min;
    }
}
