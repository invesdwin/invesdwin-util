package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ACachedCallable<E> implements Callable<E>, Supplier<E> {

    @GuardedBy("this")
    private E cached;

    @Override
    public E call() {
        if (cached == null) {
            synchronized (this) {
                if (cached == null) {
                    cached = innerCall();
                }
            }
        }
        return cached;
    }

    @Override
    public E get() {
        return call();
    }

    protected abstract E innerCall();

}
