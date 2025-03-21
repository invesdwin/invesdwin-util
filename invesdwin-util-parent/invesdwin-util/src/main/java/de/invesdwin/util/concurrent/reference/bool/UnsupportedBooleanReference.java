package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnsupportedBooleanReference implements IMutableBooleanReference {

    private static final UnsupportedBooleanReference INSTANCE = new UnsupportedBooleanReference();

    private UnsupportedBooleanReference() {}

    @Override
    public boolean get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAndSet(final boolean value) {
        throw new UnsupportedOperationException();
    }

    public static UnsupportedBooleanReference getInstance() {
        return INSTANCE;
    }

}
