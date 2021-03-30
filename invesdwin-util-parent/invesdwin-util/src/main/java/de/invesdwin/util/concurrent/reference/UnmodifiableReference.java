package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnmodifiableReference<T> implements IMutableReference<T> {

    private final T value;

    private UnmodifiableReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(final T value) {
        throw new UnsupportedOperationException();
    }

    public static <T> UnmodifiableReference<T> of(final T value) {
        return new UnmodifiableReference<>(value);
    }

}
