package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class AtomicBooleanReference implements IMutableBooleanReference, ISerializableValueObject {

    private final java.util.concurrent.atomic.AtomicBoolean value;

    public AtomicBooleanReference() {
        this.value = new java.util.concurrent.atomic.AtomicBoolean();
    }

    public AtomicBooleanReference(final boolean value) {
        this.value = new java.util.concurrent.atomic.AtomicBoolean(value);
    }

    @Override
    public boolean get() {
        return value.get();
    }

    @Override
    public void set(final boolean value) {
        this.value.set(value);
    }

    @Override
    public boolean getAndSet(final boolean value) {
        return this.value.getAndSet(value);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        final boolean get = value.get();
        return Boolean.hashCode(get);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean get = value.get();
        if (obj instanceof IBooleanReference) {
            final IBooleanReference cObj = (IBooleanReference) obj;
            return get == cObj.get();
        } else {
            return obj.equals(get);
        }
    }

}
