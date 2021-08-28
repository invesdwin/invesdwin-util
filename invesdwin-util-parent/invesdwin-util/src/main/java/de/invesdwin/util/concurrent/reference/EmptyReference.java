package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyReference<T> implements IReference<T> {

    @SuppressWarnings("rawtypes")
    private static final DisabledReference INSTANCE = new DisabledReference<>();

    @Override
    public T get() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledReference<T> getInstance() {
        return INSTANCE;
    }

}
