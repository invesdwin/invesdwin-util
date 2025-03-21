package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ALazyCallable<E> implements Callable<E>, Supplier<E> {

    @GuardedBy("this")
    private boolean initialized;
    @GuardedBy("this")
    private E cached;

    @Override
    public E call() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    cached = innerCall();
                    initialized = true;
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
