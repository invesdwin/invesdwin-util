package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledMMutableIntReference implements IMutableIntReference {

    public static final DisabledMMutableIntReference INSTANCE = new DisabledMMutableIntReference();

    private DisabledMMutableIntReference() {}

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

}
