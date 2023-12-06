package de.invesdwin.util.concurrent.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;

public interface IWrappedExecutorServiceInternal {

    String getName();

    IExecutorExceptionHandler getExecutorExceptionHandler();

    boolean isKeepThreadLocals();

    AtomicBoolean getDynamicThreadName();

    void incrementPendingCount(boolean skipWaitOnFullPendingCount) throws InterruptedException;

    void decrementPendingCount();

}
