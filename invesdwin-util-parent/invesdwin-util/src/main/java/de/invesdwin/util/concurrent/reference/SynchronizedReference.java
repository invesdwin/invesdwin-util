package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.AValueObject;

@ThreadSafe
public class SynchronizedReference<T> extends AValueObject implements IMutableReference<T> {

    private final Object lock;
    private T value;

    public SynchronizedReference() {
        this(null);
    }

    public SynchronizedReference(final Object lock) {
        this(lock, null);
    }

    public SynchronizedReference(final Object lock, final T value) {
        if (lock == null) {
            this.lock = this;
        } else {
            this.lock = lock;
        }
        this.value = value;
    }

    @Override
    public T get() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void set(final T value) {
        synchronized (lock) {
            this.value = value;
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            if (value == null) {
                return super.hashCode();
            } else {
                return value.hashCode();
            }
        }
    }

    @Override
    public boolean equals(final Object obj) {
        synchronized (lock) {
            if (value == null) {
                return super.equals(obj);
            } else if (obj instanceof IReference) {
                final IReference<?> cObj = (IReference<?>) obj;
                return value.equals(cObj.get());
            } else {
                return value.equals(obj);
            }
        }
    }

}
