package de.invesdwin.util.concurrent.reference.bool;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledMutableBooleanReference implements IMutableBooleanReference {

    public static final DisabledMutableBooleanReference INSTANCE = new DisabledMutableBooleanReference();

    private DisabledMutableBooleanReference() {}

    @Override
    public boolean get() {
        return false;
    }

    @Override
    public void set(final boolean value) {}

    @Override
    public boolean getAndSet(final boolean value) {
        return false;
    }

}
