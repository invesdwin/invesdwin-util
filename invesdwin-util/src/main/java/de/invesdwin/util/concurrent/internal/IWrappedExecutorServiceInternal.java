package de.invesdwin.util.concurrent.internal;

public interface IWrappedExecutorServiceInternal {

    String getName();

    boolean isLogExceptions();

    boolean isDynamicThreadName();

    void incrementPendingCount(boolean skipWaitOnFullPendingCount) throws InterruptedException;

    void decrementPendingCount();

}
