package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamRemoveFlatSequences<E extends ADecimal<E>> implements IStreamAlgorithm<E, E> {

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
