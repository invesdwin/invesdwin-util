package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class ExpiringFinalReference<T> extends AValueObject implements IReference {

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
