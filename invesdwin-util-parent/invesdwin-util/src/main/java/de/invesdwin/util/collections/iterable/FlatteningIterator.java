package de.invesdwin.util.collections.iterable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class FlatteningIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<? extends Iterator<? extends E>> delegate;
    private ICloseableIterator<? extends E> curIterator;

    @SafeVarargs
    public FlatteningIterator(final Iterator<? extends E>... delegate) {
        this.delegate = WrapperCloseableIterable.maybeWrap(delegate).iterator();
    }

    public FlatteningIterator(final ICloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        if (delegate.hasNext()) {
            return true;
        }
        if (curIterator != null && curIterator.hasNext()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void nextIterator() {
        curIterator.close();
        curIterator = null;
        //maybe throw another final NoSuchElement exception
        curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
    }

    @SuppressWarnings("deprecation")
    @Override
    public E next() {
        if (curIterator == null) {
            //might throw a NoSuchElement exception
            curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        while (curIterator != null) {
            try {
                return curIterator.next();
            } catch (final NoSuchElementException e) {
                nextIterator();
            }
        }
        throw new FastNoSuchElementException("FlatteningIterator: curIterator is null");
    }

    @Override
    public void close() {
        if (curIterator != null) {
            curIterator.close();
        }
        delegate.close();
    }

}
