package de.invesdwin.util.concurrent.reference.integer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnmodifiableIntReference implements IMutableIntReference {

    private final int value;

    private UnmodifiableIntReference(final int value) {
        this.value = value;
    }

    @Override
    public int get() {
        return value;
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

    public static UnmodifiableIntReference of(final int value) {
        return new UnmodifiableIntReference(value);
    }

}
