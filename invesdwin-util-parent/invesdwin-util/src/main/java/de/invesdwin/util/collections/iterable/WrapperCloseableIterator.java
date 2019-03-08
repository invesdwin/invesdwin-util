package de.invesdwin.util.collections.iterable;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public final class WrapperCloseableIterator<E> implements ICloseableIterator<E> {

    private Iterator<? extends E> delegate;

    private WrapperCloseableIterator(final Iterator<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        final boolean hasNext = delegate.hasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    @Override
    public E next() {
        try {
            return delegate.next();
        } catch (final NoSuchElementException e) {
            close();
            throw FastNoSuchElementException.maybeReplace(e, "WrapperCloseableIterator: next threw");
        }
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void close() {
        if (!(delegate instanceof EmptyCloseableIterator)) {
            if (delegate instanceof Closeable) {
                final Closeable cDelegate = (Closeable) delegate;
                try {
                    cDelegate.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                final Method close = Reflections.findMethod(delegate.getClass(), "close");
                if (close != null) {
                    Reflections.invokeMethod(close, delegate);
                }
            }
        }
        delegate = EmptyCloseableIterator.getInstance();
    }

    /**
     * Please use WrapperCloseableIterable.maybeWrap instead if possible because it does some performance optimization
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterator<T> maybeWrap(final Iterator<? extends T> iterator) {
        if (iterator instanceof ICloseableIterator) {
            return (ICloseableIterator<T>) iterator;
        } else {
            return new WrapperCloseableIterator<T>(iterator);
        }
    }

}
