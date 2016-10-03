package de.invesdwin.util.collections.iterable;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

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
        return delegate.hasNext();
    }

    @Override
    public E next() {
        assertNotClosed();
        return delegate.next();
    }

    @Override
    public void remove() {
        assertNotClosed();
        delegate.remove();
    }

    private void assertNotClosed() {
        if (closed) {
            throw new NoSuchElementException("Already closed");
        }
    }

    @Override
    public void close() {
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
