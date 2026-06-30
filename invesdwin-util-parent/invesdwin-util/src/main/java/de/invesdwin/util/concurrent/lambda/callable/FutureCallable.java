package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.future.Futures;

@ThreadSafe
public final class FutureCallable<E> implements ISafeCallable<E> {

    private Future<E> future;
    private E value;

    private FutureCallable(final Future<E> future) {
        this.future = future;
    }

    @Override
    public E call() {
        if (future != null) {
            synchronized (this) {
                final Future<E> futureCopy = future;
                if (futureCopy != null) {
                    value = Futures.getNoInterrupt(futureCopy);
                    future = null;
                }
            }
        }
        return value;
    }

    public static <T> FutureCallable<T> of(final Future<T> future) {
        return new FutureCallable<T>(future);
    }

}
