package de.invesdwin.util.concurrent.callable;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public class ImmutableCallable<E> implements Callable<E>, ISerializableValueObject {

    private final E value;

    public ImmutableCallable(final E value) {
        this.value = value;
    }

    @Override
    public E call() throws Exception {
        return value;
    }

}
