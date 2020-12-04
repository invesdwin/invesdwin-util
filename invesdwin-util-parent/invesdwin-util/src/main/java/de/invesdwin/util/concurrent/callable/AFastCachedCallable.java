package de.invesdwin.util.concurrent.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastCachedCallable<E> implements Callable<E>, Supplier<E> {

    private E cached;

    @Override
    public E call() {
        if (cached == null) {
            cached = innerCall();
        }
        return cached;
    }

    @Override
    public E get() {
        return call();
    }

    protected abstract E innerCall();

}
