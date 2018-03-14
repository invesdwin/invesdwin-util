package de.invesdwin.util.concurrent;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ACachedCallable<E> implements Callable<E> {

    @GuardedBy("this")
    private E cached;

    @Override
    public synchronized E call() {
        if (cached == null) {
            cached = innerCall();
        }
        return cached;
    }

    protected abstract E innerCall();

}
