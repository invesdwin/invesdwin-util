package de.invesdwin.util.concurrent;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ImmutableCallable<E> implements Callable<E> {

    private final E value;

    public ImmutableCallable(final E value) {
        this.value = value;
    }

    @Override
    public E call() throws Exception {
        return value;
    }

}
