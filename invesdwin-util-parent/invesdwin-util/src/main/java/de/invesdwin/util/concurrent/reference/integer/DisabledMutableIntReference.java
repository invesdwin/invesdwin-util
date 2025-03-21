package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledMutableIntReference implements IMutableIntReference {

    public static final DisabledMutableIntReference INSTANCE = new DisabledMutableIntReference();

    private DisabledMutableIntReference() {}

    @Override
    public int get() {
        return 0;
    }

    @Override
    public void set(final int value) {}

    @Override
    public int getAndSet(final int value) {
        return 0;
    }

    @Override
    public int incrementAndGet() {
        return 0;
    }

    @Override
    public int decrementAndGet() {
        return 0;
    }

}
