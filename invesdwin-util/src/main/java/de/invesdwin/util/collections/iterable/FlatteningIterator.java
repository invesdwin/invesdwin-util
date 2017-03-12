package de.invesdwin.util.collections.iterable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlatteningIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<? extends Iterator<? extends E>> delegate;
    private ICloseableIterator<? extends E> curIterator;

    public FlatteningIterator(final ICloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        try {
            return getIterator().hasNext() || delegate.hasNext();
        } catch (final NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public E next() {
        return getIterator().next();
    }

    @SuppressWarnings("deprecation")
    private ICloseableIterator<? extends E> getIterator() {
        while (curIterator == null || !curIterator.hasNext()) {
            curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        return curIterator;
    }

    @Override
    public void close() {
        if (curIterator != null) {
            curIterator.close();
        }
        delegate.close();
    }

}
