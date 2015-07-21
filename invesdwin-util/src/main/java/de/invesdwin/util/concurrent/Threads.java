package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.shutdown.ShutdownHookManager;

@Immutable
public final class Threads {

    private Threads() {}

    public static void throwIfInterrupted() throws InterruptedException {
        if (isInterrupted()) {
            throw new InterruptedException();
        }
    }

    public static boolean isInterrupted() {
        return Thread.currentThread().isInterrupted() || ShutdownHookManager.isShuttingDown();
    }

}
