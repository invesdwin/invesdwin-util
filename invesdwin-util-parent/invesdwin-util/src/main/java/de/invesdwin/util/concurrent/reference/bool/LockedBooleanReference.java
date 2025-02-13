package de.invesdwin.util.concurrent.reference.bool;

import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class LockedBooleanReference implements IMutableBooleanReference, ISerializableValueObject {

    private final Lock lock;
    private boolean value;

    public LockedBooleanReference(final Lock lock) {
        this(lock, false);
    }

    public LockedBooleanReference(final Lock lock, final boolean value) {
        this.lock = lock;
        this.value = value;
    }

    @Override
    public boolean get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean getAndSet(final boolean value) {
        lock.lock();
        try {
            final boolean get = this.value;
            this.value = value;
            return get;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(final boolean value) {
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
            return Boolean.hashCode(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        lock.lock();
        try {
            if (obj instanceof IBooleanReference) {
                final IBooleanReference cObj = (IBooleanReference) obj;
                return value == cObj.get();
            } else {
                return obj.equals(value);
            }
        } finally {
            lock.unlock();
        }
    }

}
