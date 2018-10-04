package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamRemoveFlatSequences<E extends AFDouble<E>> implements IStreamAlgorithm<E, E> {

    private E prevValue = null;

    @Override
    public E process(final E value) {
        final E returnedValue;
        if (prevValue == null || !value.equals(prevValue)) {
            returnedValue = value;
        } else {
            returnedValue = null;
        }
        prevValue = value;
        return returnedValue;
    }

    public ICloseableIterator<E> asIterator(final ICloseableIterator<? extends E> input) {
        return new ASkippingIterator<E>(input) {
            @Override
            protected boolean skip(final E element) {
                return process(element) == null;
            }
        };
    }

}
