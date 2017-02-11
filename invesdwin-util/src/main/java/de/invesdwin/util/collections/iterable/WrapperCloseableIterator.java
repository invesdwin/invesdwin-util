package de.invesdwin.util.collections.iterable;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public final class WrapperCloseableIterator<E> implements ICloseableIterator<E> {

    private final Iterator<? extends E> delegate;
    private boolean closed;

    private WrapperCloseableIterator(final Iterator<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        if (closed) {
            return false;
        }
        final boolean hasNext = delegate.hasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    @Override
    public E next() {
        if (closed) {
            throw new FastNoSuchElementException("WrapperCloseableIterator: next already closed");
        }
        try {
            return delegate.next();
        } catch (final NoSuchElementException e) {
            close();
            throw FastNoSuchElementException.maybeReplace(e,
                    "WrapperCloseableIterator: next threw NoSuchElementException");
        }
    }

    @Override
    public void remove() {
        if (closed) {
            throw new FastNoSuchElementException("WrapperCloseableIterator: remove already closed");
        }
        delegate.remove();
    }

    @Override
    public void close() {
        closed = true;
        final Method close = Reflections.findMethod(delegate.getClass(), "close");
        if (close != null) {
            Reflections.invokeMethod(close, delegate);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterator<T> maybeWrap(final Iterator<? extends T> iterator) {
        if (iterator instanceof ICloseableIterator) {
            return (ICloseableIterator<T>) iterator;
        } else {
            return new WrapperCloseableIterator<T>(iterator);
        }
    }

}
