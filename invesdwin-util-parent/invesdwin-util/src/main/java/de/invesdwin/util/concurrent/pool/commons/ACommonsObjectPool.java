package de.invesdwin.util.concurrent.pool.commons;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.pool2.ObjectPool;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.pool.ICloseableObjectPool;

/**
 * Implements the lifecycle of pooled objects
 */
@ThreadSafe
public abstract class ACommonsObjectPool<E> implements ObjectPool<E>, ICloseableObjectPool<E> {

    protected ICommonsPoolableObjectFactory<E> factory;
    private final AtomicInteger activeCount = new AtomicInteger();
    @GuardedBy("this")
    private boolean closed;

    public ACommonsObjectPool(final ICommonsPoolableObjectFactory<E> factory) {
        this.factory = factory;
    }

    @Override
    public final E borrowObject() {
        throwIfClosed();
        E element;
        while (true) {
            try {
                Threads.throwIfInterrupted();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            element = internalBorrowObject();
            if (element != null) {
                factory.activateObject(element);
                if (factory.validateObject(element)) {
                    break;
                } else {
                    factory.destroyObject(element);
                }
            } else {
                //CHECKSTYLE:OFF we explicitly want the stacktrace here
                throw new NoSuchElementException("internalBorrow() returned null");
                //CHECKSTYLE:ON
            }
        }
        activeCount.incrementAndGet();
        return element;
    }

    @Override
    public final void addObject() {
        throwIfClosed();
        final E element = internalAddObject();
        if (factory.validateObject(element)) {
            if (!factory.passivateObject(element)) {
                removeObject(element);
            }
        } else {
            removeObject(element);
        }
    }

    protected void removeObject(final E element) {
        internalRemoveObject(element);
        factory.destroyObject(element);
    }

    @Override
    public final void returnObject(final E element) {
        if (element != null) {
            activeCount.decrementAndGet();
            if (factory.validateObject(element)) {
                if (factory.passivateObject(element)) {
                    internalReturnObject(element);
                } else {
                    factory.destroyObject(element);
                }
            } else {
                factory.destroyObject(element);
            }
        }
    }

    @Override
    public final void invalidateObject(final E element) {
        if (element != null) {
            activeCount.decrementAndGet();
            factory.destroyObject(element);
            internalInvalidateObject(element);
        }
    }

    @Override
    public final void clear() {
        for (final E element : internalClear()) {
            factory.destroyObject(element);
        }
    }

    /**
     * Counts all active and idle objects.
     */
    public final int size() {
        return getNumIdle() + getNumActive();
    }

    /*************************** templates ******************************************/

    protected abstract E internalBorrowObject();

    /**
     * Returns the added object.
     */
    protected abstract E internalAddObject();

    /**
     * If during the add the validation failed and destroy was called, the instance must be removed afterall.
     */
    protected abstract void internalRemoveObject(E element);

    protected abstract void internalReturnObject(E element);

    protected abstract void internalInvalidateObject(E element);

    @Override
    public abstract int getNumIdle();

    /**
     * Returns the removed idle objects.
     */
    public abstract Collection<E> internalClear();

    /**************************** unchangeable impl ***************************/

    @Override
    public final int getNumActive() {
        return activeCount.get();
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            try {
                clear();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            closed = true;
        }
    }

    public final synchronized boolean isClosed() {
        return closed;
    }

    protected final void throwIfClosed() {
        if (isClosed()) {
            throw new IllegalStateException("closed");
        }
    }

    public final void setFactory(final ICommonsPoolableObjectFactory<E> factory) {
        this.factory = factory;
    }

}
