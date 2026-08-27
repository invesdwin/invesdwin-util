package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Runnables {

    public static final Runnable NOOP = () -> {
    };

    private Runnables() {}

}
