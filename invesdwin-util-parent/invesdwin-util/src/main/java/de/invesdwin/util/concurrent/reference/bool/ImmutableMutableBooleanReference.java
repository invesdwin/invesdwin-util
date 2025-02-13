package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableMutableBooleanReference implements IMutableBooleanReference {

    private final boolean value;

    private ImmutableMutableBooleanReference(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean get() {
        return value;
    }

    @Override
    public void set(final boolean value) {
        //noop
    }

    @Override
    public boolean getAndSet(final boolean value) {
        return this.value;
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

    public static ImmutableMutableBooleanReference of(final boolean value) {
        return new ImmutableMutableBooleanReference(value);
    }

}
