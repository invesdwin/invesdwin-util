package de.invesdwin.util.concurrent.reference;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * WARNING: Instances of this object should always be defined in static variables or else the threadLocal instances
 * might cause memory leaks.
 * 
 * Use WeakThreadLocalReference instead for instance thread locals without memory leaks.
 */
@ThreadSafe
public class ThreadLocalReference<T> implements IMutableReference<T> {

    private final FastThreadLocal<Optional<T>> threadLocal = new FastThreadLocal<Optional<T>>();

    protected T initialValue() {
        return null;
    }

    @Override
    public T get() {
        final Optional<T> optional = threadLocal.get();
        if (optional != null) {
            return optional.orElse(null);
        } else {
            final T initialValue = initialValue();
            threadLocal.set(Optional.ofNullable(initialValue));
            return initialValue;
        }
    }

    @Override
    public void set(final T value) {
        if (value == null) {
            threadLocal.remove();
        } else {
            threadLocal.set(Optional.ofNullable(value));
        }
    }

    public void remove() {
        threadLocal.remove();
    }

}
