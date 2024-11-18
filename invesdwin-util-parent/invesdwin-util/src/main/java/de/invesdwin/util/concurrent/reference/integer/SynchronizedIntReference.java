package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class SynchronizedIntReference implements IMutableIntReference, ISerializableValueObject {

    private final Object lock;
    private int value;

    public SynchronizedIntReference() {
        this(null);
    }

    public SynchronizedIntReference(final Object lock) {
        this(lock, 0);
    }

    public SynchronizedIntReference(final Object lock, final int value) {
        if (lock == null) {
            this.lock = this;
        } else {
            this.lock = lock;
        }
        this.value = value;
    }

    @Override
    public int get() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void set(final int value) {
        synchronized (lock) {
            this.value = value;
        }
    }

    @Override
    public int getAndSet(final int value) {
        synchronized (lock) {
            final int get = this.value;
            this.value = value;
            return get;
        }
    }

    @Override
    public int incrementAndGet() {
        synchronized (lock) {
            return ++value;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return Integer.hashCode(value);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        synchronized (lock) {
            if (obj instanceof IIntReference) {
                final IIntReference cObj = (IIntReference) obj;
                return value == cObj.get();
            } else {
                return obj.equals(value);
            }
        }
    }

}
