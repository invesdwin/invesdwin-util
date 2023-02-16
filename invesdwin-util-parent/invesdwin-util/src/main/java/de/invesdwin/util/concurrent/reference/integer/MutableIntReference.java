package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class MutableIntReference implements IMutableIntReference, ISerializableValueObject {

    private int value;

    public MutableIntReference() {}

    public MutableIntReference(final int value) {
        this.value = value;
    }

    @Override
    public int get() {
        return value;
    }

    @Override
    public void set(final int value) {
        this.value = value;
    }

    @Override
    public int getAndSet(final int value) {
        final int get = this.value;
        this.value = value;
        return get;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IIntReference) {
            final IIntReference cObj = (IIntReference) obj;
            return value == cObj.get();
        } else {
            return obj.equals(value);
        }
    }

}
