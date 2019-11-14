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

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasNext() {
        if (curIterator == null && delegate.hasNext()) {
            curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        while (curIterator != null) {
            if (curIterator.hasNext()) {
                return true;
            } else if (delegate.hasNext()) {
                try {
                    nextIterator();
                } catch (final NoSuchElementException e) {
                    //might happen on null value
                    return false;
                }
            } else {
                break;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void nextIterator() {
        curIterator.close();
        curIterator = null;
        //might throw another final NoSuchElement exception
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
