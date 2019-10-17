package de.invesdwin.util.concurrent.future;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Callables {

    private Callables() {}

    public static <V> V call(final Callable<V> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
