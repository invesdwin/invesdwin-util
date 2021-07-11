package de.invesdwin.util.concurrent.lambda;

import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public class ImmutableSupplier<E> implements Supplier<E>, ISerializableValueObject {

    private final E value;

    public ImmutableSupplier(final E value) {
        this.value = value;
    }

    @Override
    public E get() {
        return value;
    }

    public static <T> ImmutableSupplier<T> of(final T value) {
        return new ImmutableSupplier<T>(value);
    }

}
