package de.invesdwin.util.collections.iterable;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class WrapperCloseableIterator<E> implements ICloseableIterator<E> {

    private final Iterator<? extends E> delegate;

    public WrapperCloseableIterator(final Iterator<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        return delegate.next();
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void close() {
        final Method close = Reflections.findMethod(delegate.getClass(), "close");
        if (close != null) {
            Reflections.invokeMethod(close, delegate);
        }
    }

    public static <T> ICloseableIterator<T> maybeWrap(final Iterator<T> iterator) {
        if (iterator instanceof ACloseableIterator) {
            return (ACloseableIterator<T>) iterator;
        } else {
            return new WrapperCloseableIterator<T>(iterator);
        }
    }

}
