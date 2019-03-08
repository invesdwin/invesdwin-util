package de.invesdwin.util.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public final class ImmutableFuture<E> implements Future<E>, ISerializableValueObject {

    private final E value;

    private ImmutableFuture(final E value) {
        this.value = value;
    }

    public static <E> ImmutableFuture<E> of(final E value) {
        return new ImmutableFuture<E>(value);
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
        return value;
    }

    @Override
    public E get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return value;
    }

}
