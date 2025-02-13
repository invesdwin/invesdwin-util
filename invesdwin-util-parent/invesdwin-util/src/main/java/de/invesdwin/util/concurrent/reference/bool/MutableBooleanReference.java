package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class MutableBooleanReference implements IMutableBooleanReference, ISerializableValueObject {

    private boolean value;

    public MutableBooleanReference() {}

    public MutableBooleanReference(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean get() {
        return value;
    }

    @Override
    public void set(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean getAndSet(final boolean value) {
        final boolean get = this.value;
        this.value = value;
        return get;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IBooleanReference) {
            final IBooleanReference cObj = (IBooleanReference) obj;
            return value == cObj.get();
        } else {
            return obj.equals(value);
        }
    }

}
