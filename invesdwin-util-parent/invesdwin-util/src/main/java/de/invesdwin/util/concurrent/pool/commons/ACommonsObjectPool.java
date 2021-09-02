package de.invesdwin.util.concurrent.pool.commons;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.pool2.ObjectPool;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.pool.IObjectPool;

/**
 * Implements the lifecycle of pooled objects
 */
@ThreadSafe
public abstract class ACommonsObjectPool<E> implements ObjectPool<E>, IObjectPool<E> {

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
        E obj;
        while (true) {
            try {
                Threads.throwIfInterrupted();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            obj = internalBorrowObject();
            if (obj != null) {
                factory.activateObject(obj);
                if (factory.validateObject(obj)) {
                    break;
                } else {
                    factory.destroyObject(obj);
                }
            } else {
                //CHECKSTYLE:OFF we explicitly want the stacktrace here
                throw new NoSuchElementException("internalBorrow() returned null");
                //CHECKSTYLE:ON
            }
        }
        activeCount.incrementAndGet();
        return obj;
    }

    @Override
    public final void addObject() {
        throwIfClosed();
        final E obj = internalAddObject();
        if (factory.validateObject(obj)) {
            factory.passivateObject(obj);
        } else {
            removeObject(obj);
        }
    }

    protected void removeObject(final E obj) {
        internalRemoveObject(obj);
        factory.destroyObject(obj);
    }

    @Override
    public final void returnObject(final E obj) {
        if (obj != null) {
            activeCount.decrementAndGet();
            if (factory.validateObject(obj)) {
                factory.passivateObject(obj);
                internalReturnObject(obj);
            } else {
                factory.destroyObject(obj);
            }
        }
    }

    @Override
    public final void invalidateObject(final E obj) {
        if (obj != null) {
            activeCount.decrementAndGet();
            factory.destroyObject(obj);
            internalInvalidateObject(obj);
        }
    }

    @Override
    public final void clear() {
        for (final E obj : internalClear()) {
            factory.destroyObject(obj);
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
    protected abstract void internalRemoveObject(E obj);

    protected abstract void internalReturnObject(E obj);

    protected abstract void internalInvalidateObject(E obj);

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
        Assertions.assertThat(isClosed()).as("Instance already closed!").isFalse();
    }

    public final void setFactory(final ICommonsPoolableObjectFactory<E> factory) {
        this.factory = factory;
    }

}
