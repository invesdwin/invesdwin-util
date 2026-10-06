package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Runnables {

    public static final Runnable NOOP = () -> {
    };
    public static final Runnable[] EMPTY_ARRAY = new Runnable[0];

    private Runnables() {}

}
