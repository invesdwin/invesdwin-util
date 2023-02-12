package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class VolatileReference<T> implements IMutableReference<T>, ISerializableValueObject {

    private volatile T value;

    public VolatileReference() {}

    public VolatileReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(final T value) {
        this.value = value;
    }

    @Override
    public T getAndSet(final T value) {
        final T get = this.value;
        this.value = value;
        return get;
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
