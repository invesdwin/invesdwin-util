package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class ExpiringFinalReference<T> implements IReference<T>, ISerializableValueObject {

    private volatile Instant lastAccess = new Instant();
    private final T value;

    public ExpiringFinalReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        lastAccess = new Instant();
        return value;
    }

    public boolean isExpired(final Duration timeout) {
        return lastAccess.isGreaterThan(timeout);
    }

    public Instant getLastAccess() {
        return lastAccess;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return super.hashCode();
        } else {
            return value.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
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
