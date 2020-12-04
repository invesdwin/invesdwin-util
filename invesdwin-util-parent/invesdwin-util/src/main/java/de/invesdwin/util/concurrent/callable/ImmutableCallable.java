package de.invesdwin.util.concurrent.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public class ImmutableCallable<E> implements Callable<E>, Supplier<E>, ISerializableValueObject {

    private final E value;

    public ImmutableCallable(final E value) {
        this.value = value;
    }

    @Override
    public E call() throws Exception {
        return value;
    }

    @Override
    public E get() {
        return value;
    }

}
