package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class LockedReference<T> implements IMutableReference<T>, ISerializableValueObject {

    private final ILock lock;
    private T value;

    public LockedReference(final ILock lock) {
        this(lock, null);
    }

    public LockedReference(final ILock lock, final T value) {
        this.lock = lock;
        this.value = value;
    }

    @Override
    public T get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T getAndSet(final T value) {
        lock.lock();
        try {
            final T get = this.value;
            this.value = value;
            return get;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(final T value) {
        lock.lock();
        try {
            this.value = value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            if (value == null) {
                return super.hashCode();
            } else {
                return value.hashCode();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        lock.lock();
        try {
            if (value == null) {
                return super.equals(obj);
            } else if (obj instanceof IReference) {
                final IReference<?> cObj = (IReference<?>) obj;
                return value.equals(cObj.get());
            } else {
                return value.equals(obj);
            }
        } finally {
            lock.unlock();
        }
    }

}
