package de.invesdwin.util.concurrent.internal;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IWrappedExecutorServiceInternal {

    String getName();

    boolean isLogExceptions();

    boolean isKeepThreadLocals();

    AtomicBoolean getDynamicThreadName();

    void incrementPendingCount(boolean skipWaitOnFullPendingCount) throws InterruptedException;

    void decrementPendingCount();

}
