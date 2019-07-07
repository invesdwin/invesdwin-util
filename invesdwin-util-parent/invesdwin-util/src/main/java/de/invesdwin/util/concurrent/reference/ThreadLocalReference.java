package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public class ThreadLocalReference<T> implements IMutableReference<T> {

    private final FastThreadLocal<T> threadLocal = new FastThreadLocal<>();

    @Override
    public T get() {
        return threadLocal.get();
    }

    @Override
    public void set(final T value) {
        if (value == null) {
            threadLocal.remove();
        } else {
            threadLocal.set(value);
        }
    }

}
