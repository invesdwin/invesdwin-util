package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedObjectPool<E> implements IObjectPool<E> {

    private final IObjectPool<E> delegate;
    private final Object lock;

    public SynchronizedObjectPool(final IObjectPool<E> delegate) {
        this(delegate, delegate);
    }

    public SynchronizedObjectPool(final IObjectPool<E> delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public E borrowObject() {
        synchronized (lock) {
            return delegate.borrowObject();
        }
    }

    @Override
    public void returnObject(final E element) {
        synchronized (lock) {
            delegate.returnObject(element);
        }
    }

    @Override
    public void invalidateObject(final E element) {
        synchronized (lock) {
            delegate.invalidateObject(element);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

    public static <T> SynchronizedObjectPool<T> valueOf(final IObjectPool<T> delegate) {
        return new SynchronizedObjectPool<>(delegate);
    }

}
