package de.invesdwin.util.collections.iterable;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class WrapperCloseableIterator<E> extends ACloseableIterator<E> {

    private final Iterator<? extends E> delegate;

    public WrapperCloseableIterator(final Iterator<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected boolean innerHasNext() {
        return delegate.hasNext();
    }

    @Override
    protected E innerNext() {
        return delegate.next();
    }

    @Override
    protected void innerRemove() {
        delegate.remove();
    }

    @Override
    protected void innerClose() {
        final Method close = Reflections.findMethod(delegate.getClass(), "close");
        if (close != null) {
            Reflections.invokeMethod(close, delegate);
        }
    }

    public static <T> ACloseableIterator<T> maybeWrap(final Iterator<T> iterator) {
        if (iterator instanceof ACloseableIterator) {
            return (ACloseableIterator<T>) iterator;
        } else {
            return new WrapperCloseableIterator<T>(iterator);
        }
    }

}
