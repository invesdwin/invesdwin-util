package de.invesdwin.util.concurrent.lambda.callable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.lambda.ImmutableSupplier;

@Immutable
public class ImmutableCallable<E> extends ImmutableSupplier<E> implements ISafeCallable<E> {

    public ImmutableCallable(final E value) {
        super(value);
    }

    @Override
    public E call() {
        return get();
    }

    public static <T> ImmutableCallable<T> of(final T value) {
        return new ImmutableCallable<T>(value);
    }

}
