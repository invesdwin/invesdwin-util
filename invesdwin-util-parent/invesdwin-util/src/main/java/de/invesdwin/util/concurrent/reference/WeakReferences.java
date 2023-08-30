package de.invesdwin.util.concurrent.reference;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class WeakReferences<E> {

    @SuppressWarnings("rawtypes")
    private static final WeakReference EMPTY = new WeakReference<>(null);

    private WeakReferences() {}

    @SuppressWarnings("unchecked")
    public static <T> WeakReference<T> getEmpty() {
        return EMPTY;
    }

    public static <T> WeakReference<T> newInstance(final T value) {
        if (value == null) {
            return getEmpty();
        } else {
            return new WeakReference<T>(value);
        }
    }

}
