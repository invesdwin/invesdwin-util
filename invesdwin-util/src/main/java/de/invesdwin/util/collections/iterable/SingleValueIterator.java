package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

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
            throw new NoSuchElementException();
        }
    }

    @Override
    public void close() {
        singleValue = null;
    }

}
