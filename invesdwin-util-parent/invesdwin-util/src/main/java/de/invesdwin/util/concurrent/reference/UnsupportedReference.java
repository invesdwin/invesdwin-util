package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnsupportedReference<T> implements IMutableReference<T> {

    @SuppressWarnings("rawtypes")
    private static final UnsupportedReference INSTANCE = new UnsupportedReference<>();

    private UnsupportedReference() {
    }

    @Override
    public T get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getAndSet(final T value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> UnsupportedReference<T> getInstance() {
        return INSTANCE;
    }

}
