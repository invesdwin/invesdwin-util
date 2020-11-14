package de.invesdwin.util.concurrent.callable;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastCachedCallable<E> implements Callable<E> {

    private E cached;

    @Override
    public E call() {
        if (cached == null) {
            cached = innerCall();
        }
        return cached;
    }

    protected abstract E innerCall();

}
