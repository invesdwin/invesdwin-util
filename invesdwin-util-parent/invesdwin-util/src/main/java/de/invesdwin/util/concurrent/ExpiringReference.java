package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class ExpiringReference<T> extends AValueObject {

    private volatile Instant lastAccess = new Instant();
    private final T value;

    public ExpiringReference(final T value) {
        this.value = value;
    }

    public T get() {
        lastAccess = new Instant();
        return value;
    }

    public boolean isExpired(final Duration timeout) {
        return lastAccess.toDuration().isGreaterThan(timeout);
    }

    public Instant getLastAccess() {
        return lastAccess;
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
        } else if (obj instanceof ExpiringReference) {
            final ExpiringReference<?> cObj = (ExpiringReference<?>) obj;
            return value.equals(cObj.value);
        } else {
            return value.equals(obj);
        }
    }

}
