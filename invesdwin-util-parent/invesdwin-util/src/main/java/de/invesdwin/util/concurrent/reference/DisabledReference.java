package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledReference<T> implements IMutableReference<T> {

    @SuppressWarnings("rawtypes")
    private static final DisabledReference INSTANCE = new DisabledReference<>();

    @Override
    public T get() {
        return null;
    }

    @Override
    public void set(final T value) {
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledReference<T> getInstance() {
        return INSTANCE;
    }

}
