package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledCallable<V> implements Callable<V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledCallable INSTANCE = new DisabledCallable();

    @Override
    public V call() throws Exception {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledCallable<T> getInstance() {
        return INSTANCE;
    }

}
