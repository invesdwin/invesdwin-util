package de.invesdwin.util.concurrent;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class MutableCallable<E> implements Callable<E> {

    private E value;

    public void setValue(final E value) {
        this.value = value;
    }

    @Override
    public E call() throws Exception {
        return value;
    }

}
