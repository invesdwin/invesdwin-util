package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableBooleanReference implements IBooleanReference {

    private final boolean value;

    private ImmutableBooleanReference(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean get() {
        return value;
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

    public static ImmutableBooleanReference of(final boolean value) {
        return new ImmutableBooleanReference(value);
    }

}
