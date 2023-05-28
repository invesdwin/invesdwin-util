package de.invesdwin.util.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class APostProcessingFuture<E> implements Future<E> {

    private final Future<E> delegate;
    private boolean finished;
    private E finishedValue;
    private ExecutionException finishedException;

    public APostProcessingFuture(final Future<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        finished = true;
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        final boolean cancelled = delegate.isCancelled();
        if (cancelled) {
            finished = true;
        }
        return cancelled;
    }

    @Override
    public boolean isDone() {
        if (finished) {
            return true;
        } else {
            final boolean done = delegate.isDone();
            if (done && !finished) {
                finished = true;
                if (!delegate.isCancelled()) {
                    try {
                        final E value = delegate.get();
                        finishedValue = onSuccess(value);
                    } catch (final ExecutionException e) {
                        finishedException = onError(e);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return done;
        }
    }

    protected abstract E onSuccess(E value) throws ExecutionException;

    protected abstract ExecutionException onError(ExecutionException exc);

    @Override
    public E get() throws InterruptedException, ExecutionException {
        if (finished) {
            if (finishedException != null) {
                throw finishedException;
            } else {
                return finishedValue;
            }
        } else {
            try {
                final E value = delegate.get();
                finishedValue = onSuccess(value);
                finished = true;
                return finishedValue;
            } catch (final ExecutionException e) {
                finishedException = onError(e);
                finished = true;
                throw finishedException;
            }
        }
    }

    @Override
    public E get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (finished) {
            if (finishedException != null) {
                throw finishedException;
            } else {
                return finishedValue;
            }
        } else {
            try {
                final E value = delegate.get(timeout, unit);
                finishedValue = onSuccess(value);
                finished = true;
                return finishedValue;
            } catch (final ExecutionException e) {
                finishedException = onError(e);
                finished = true;
                throw finishedException;
            }
        }
    }

}
