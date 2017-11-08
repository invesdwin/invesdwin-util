package de.invesdwin.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ADelegateFuture<E> implements Future<E> {

    private final Future<E> delegate;

    public ADelegateFuture() {
        this.delegate = getDelegate();
    }

    protected abstract Future<E> getDelegate();

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public E get() throws InterruptedException, ExecutionException {
        final E get = delegate.get();
        if (delegate.isDone()) {
            onDone(get);
        }
        return get;
    }

    @Override
    public E get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        final E get = delegate.get(timeout, unit);
        if (delegate.isDone()) {
            onDone(get);
        }
        return get;
    }

    protected void onDone(final E get) {}

}
