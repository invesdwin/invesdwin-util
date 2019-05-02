package de.invesdwin.util.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public final class ThrowableFuture<E> implements Future<E>, ISerializableValueObject {

    private final Throwable throwable;

    private ThrowableFuture(final Throwable throwable) {
        this.throwable = throwable;
    }

    public static <E> ThrowableFuture<E> of(final Throwable throwable) {
        return new ThrowableFuture<E>(throwable);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public E get() throws InterruptedException, ExecutionException {
        throw new ExecutionException(throwable);
    }

    @Override
    public E get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new ExecutionException(throwable);
    }

}
