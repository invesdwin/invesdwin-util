package de.invesdwin.util.concurrent.reference.integer;

import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class LockedIntReference implements IMutableIntReference, ISerializableValueObject {

    private final Lock lock;
    private int value;

    public LockedIntReference(final Lock lock) {
        this(lock, 0);
    }

    public LockedIntReference(final Lock lock, final int value) {
        this.lock = lock;
        this.value = value;
    }

    @Override
    public int get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getAndSet(final int value) {
        lock.lock();
        try {
            final int get = this.value;
            this.value = value;
            return get;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(final int value) {
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
            return Integer.hashCode(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        lock.lock();
        try {
            if (obj instanceof IIntReference) {
                final IIntReference cObj = (IIntReference) obj;
                return value == cObj.get();
            } else {
                return obj.equals(value);
            }
        } finally {
            lock.unlock();
        }
    }

}
