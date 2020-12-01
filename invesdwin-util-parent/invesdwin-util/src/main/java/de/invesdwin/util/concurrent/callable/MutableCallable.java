package de.invesdwin.util.concurrent.callable;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public class MutableCallable<E> implements Callable<E>, ISerializableValueObject {

    private E value;

    public void setValue(final E value) {
        this.value = value;
    }

    @Override
    public E call() {
        return value;
    }

}
