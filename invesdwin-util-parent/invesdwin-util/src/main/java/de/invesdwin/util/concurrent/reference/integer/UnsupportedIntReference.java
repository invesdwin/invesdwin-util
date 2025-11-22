package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnsupportedIntReference implements IMutableIntReference {

    private static final UnsupportedIntReference INSTANCE = new UnsupportedIntReference();

    private UnsupportedIntReference() {}

    @Override
    public int get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAndSet(final int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int incrementAndGet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int decrementAndGet() {
        throw new UnsupportedOperationException();
    }

    public static UnsupportedIntReference getInstance() {
        return INSTANCE;
    }

}
