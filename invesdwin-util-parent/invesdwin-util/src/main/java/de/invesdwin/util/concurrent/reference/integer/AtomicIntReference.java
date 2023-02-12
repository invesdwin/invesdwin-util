package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class AtomicIntReference implements IMutableIntReference, ISerializableValueObject {

    private final java.util.concurrent.atomic.AtomicInteger value;

    public AtomicIntReference() {
        this.value = new java.util.concurrent.atomic.AtomicInteger();
    }

    public AtomicIntReference(final int value) {
        this.value = new java.util.concurrent.atomic.AtomicInteger(value);
    }

    @Override
    public int get() {
        return value.get();
    }

    @Override
    public void set(final int value) {
        this.value.set(value);
    }

    @Override
    public int getAndSet(final int value) {
        return this.value.getAndSet(value);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        final int get = value.get();
        return Integer.hashCode(get);
    }

    @Override
    public boolean equals(final Object obj) {
        final int get = value.get();
        if (obj instanceof IIntReference) {
            final IIntReference cObj = (IIntReference) obj;
            return get == cObj.get();
        } else {
            return obj.equals(get);
        }
    }

}
