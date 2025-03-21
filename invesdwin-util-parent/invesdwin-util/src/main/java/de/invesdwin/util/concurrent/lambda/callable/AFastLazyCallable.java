package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastLazyCallable<E> implements Callable<E>, Supplier<E> {

    private boolean initialized;
    private E cached;

    @Override
    public E call() {
        if (!initialized) {
            cached = innerCall();
            initialized = true;
        }
        return cached;
    }

    @Override
    public E get() {
        return call();
    }

    protected abstract E innerCall();

}
