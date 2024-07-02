package de.invesdwin.util.concurrent.reference.lazy;

import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedLazyReference<T> implements ILazyReference<T> {

    private final Supplier<T> factory;
    private volatile T value;

    public SynchronizedLazyReference(final Supplier<T> factory) {
        this.factory = factory;
    }

    @Override
    public T get() {
        T v = value;
        if (v == null) {
            synchronized (this) {
                v = value;
                if (v == null) {
                    v = factory.get();
                    value = v;
                }
            }
        }
        return v;
    }

    @Override
    public T getIfNotNull() {
        return value;
    }

    @Override
    public void set(final T value) {
        this.value = value;
    }

    @Override
    public T getAndSet(final T value) {
        final T get = this.value;
        this.value = value;
        return get;
    }

}
