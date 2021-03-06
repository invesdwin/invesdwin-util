package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public class MutableCallable<E> implements Callable<E>, Supplier<E>, ISerializableValueObject {

    private E value;

    public MutableCallable(final E initialValue) {
        this.value = initialValue;
    }

    public MutableCallable() {

    }

    public void setValue(final E value) {
        this.value = value;
    }

    @Override
    public E call() {
        return value;
    }

    @Override
    public E get() {
        return value;
    }

}
