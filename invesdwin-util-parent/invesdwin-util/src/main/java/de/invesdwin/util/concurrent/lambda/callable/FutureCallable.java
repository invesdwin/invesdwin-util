package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.future.Futures;

@ThreadSafe
public final class FutureCallable<E> implements ISafeCallable<E> {

    private Supplier<E> value;

    private FutureCallable(final Future<E> future) {
        this.value = () -> {
            final E newValue = Futures.getNoInterrupt(future);
            value = () -> newValue;
            return newValue;
        };
    }

    @Override
    public E call() {
        return value.get();
    }

    public static <T> FutureCallable<T> of(final Future<T> future) {
        return new FutureCallable<T>(future);
    }

}
