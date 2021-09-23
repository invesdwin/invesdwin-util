package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class AtomicReference<T> implements IMutableReference<T>, ISerializableValueObject {

    private final java.util.concurrent.atomic.AtomicReference<T> value;

    public AtomicReference() {
        this.value = new java.util.concurrent.atomic.AtomicReference<>();
    }

    public AtomicReference(final T value) {
        this.value = new java.util.concurrent.atomic.AtomicReference<>(value);
    }

    @Override
    public T get() {
        return value.get();
    }

    @Override
    public void set(final T value) {
        this.value.set(value);
    }

    @Override
    public T getAndSet(final T value) {
        return this.value.getAndSet(value);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        final T get = value.get();
        if (get == null) {
            return super.hashCode();
        } else {
            return get.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        final T get = value.get();
        if (get == null) {
            return super.equals(obj);
        } else if (obj instanceof IReference) {
            final IReference<?> cObj = (IReference<?>) obj;
            return get.equals(cObj.get());
        } else {
            return get.equals(obj);
        }
    }

}
