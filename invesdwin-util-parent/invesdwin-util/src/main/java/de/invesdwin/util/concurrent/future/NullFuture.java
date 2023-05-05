package de.invesdwin.util.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public final class NullFuture<E> implements Future<E>, ISerializableValueObject {

    @SuppressWarnings("rawtypes")
    private static final NullFuture INSTANCE = new NullFuture<>();

    private NullFuture() {}

    @SuppressWarnings("unchecked")
    public static <E> NullFuture<E> getInstance() {
        return INSTANCE;
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
        return null;
    }

    @Override
    public E get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

}
