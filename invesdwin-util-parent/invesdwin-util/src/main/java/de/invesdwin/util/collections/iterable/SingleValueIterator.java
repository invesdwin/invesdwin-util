package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class SingleValueIterator<E> implements ICloseableIterator<E> {

    private E singleValue;

    public SingleValueIterator(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    public boolean hasNext() {
        return singleValue != null;
    }

    @Override
    public E next() {
        if (hasNext()) {
            final E ret = singleValue;
            singleValue = null;
            return ret;
        } else {
            throw new FastNoSuchElementException("SingleValueIterator: hasNext is false");
        }
    }

    @Override
    public void close() {
        singleValue = null;
    }

}
