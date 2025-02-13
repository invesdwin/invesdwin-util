package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnmodifiableBooleanReference implements IMutableBooleanReference {

    private final boolean value;

    private UnmodifiableBooleanReference(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean get() {
        return value;
    }

    @Override
    public void set(final boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAndSet(final boolean value) {
        throw new UnsupportedOperationException();
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

    public static UnmodifiableBooleanReference of(final boolean value) {
        return new UnmodifiableBooleanReference(value);
    }

}
