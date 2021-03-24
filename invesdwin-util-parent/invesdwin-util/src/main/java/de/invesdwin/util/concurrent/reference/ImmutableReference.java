package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableReference<T> implements IReference<T> {

    private final T value;

    private ImmutableReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    public static <T> ImmutableReference<T> of(final T value) {
        return new ImmutableReference<>(value);
    }

}
