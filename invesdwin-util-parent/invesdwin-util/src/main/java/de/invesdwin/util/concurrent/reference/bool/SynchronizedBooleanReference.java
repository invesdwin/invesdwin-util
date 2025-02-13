package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class SynchronizedBooleanReference implements IMutableBooleanReference, ISerializableValueObject {

    private final Object lock;
    private boolean value;

    public SynchronizedBooleanReference() {
        this(null);
    }

    public SynchronizedBooleanReference(final Object lock) {
        this(lock, false);
    }

    public SynchronizedBooleanReference(final Object lock, final boolean value) {
        if (lock == null) {
            this.lock = this;
        } else {
            this.lock = lock;
        }
        this.value = value;
    }

    @Override
    public boolean get() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void set(final boolean value) {
        synchronized (lock) {
            this.value = value;
        }
    }

    @Override
    public boolean getAndSet(final boolean value) {
        synchronized (lock) {
            final boolean get = this.value;
            this.value = value;
            return get;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return Boolean.hashCode(value);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        synchronized (lock) {
            if (obj instanceof IBooleanReference) {
                final IBooleanReference cObj = (IBooleanReference) obj;
                return value == cObj.get();
            } else {
                return obj.equals(value);
            }
        }
    }

}
