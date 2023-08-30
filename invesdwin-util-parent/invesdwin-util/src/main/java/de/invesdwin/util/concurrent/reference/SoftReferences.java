package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class SoftReferences<E> {

    @SuppressWarnings("rawtypes")
    private static final SoftReference EMPTY = new SoftReference<>(null);

    private SoftReferences() {}

    @SuppressWarnings("unchecked")
    public static <T> SoftReference<T> getEmpty() {
        return EMPTY;
    }

    public static <T> SoftReference<T> newInstance(final T value) {
        if (value == null) {
            return getEmpty();
        } else {
            return new SoftReference<T>(value);
        }
    }

}
