package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableMutableIntReference implements IMutableIntReference {

    private final int value;

    private ImmutableMutableIntReference(final int value) {
        this.value = value;
    }

    @Override
    public int get() {
        return value;
    }

    @Override
    public void set(final int value) {
        //noop
    }

    @Override
    public int getAndSet(final int value) {
        return this.value;
    }

    @Override
    public int incrementAndGet() {
        return value;
    }

    @Override
    public int decrementAndGet() {
        return value;
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

    public static ImmutableMutableIntReference of(final int value) {
        return new ImmutableMutableIntReference(value);
    }

}
