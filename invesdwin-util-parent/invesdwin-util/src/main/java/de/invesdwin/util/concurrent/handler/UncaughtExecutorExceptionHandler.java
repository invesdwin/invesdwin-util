package de.invesdwin.util.concurrent.handler;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UncaughtExecutorExceptionHandler implements IExecutorExceptionHandler {

    public static final UncaughtExecutorExceptionHandler INSTANCE = new UncaughtExecutorExceptionHandler();

    private UncaughtExecutorExceptionHandler() {}

    @Override
    public Throwable handleExecutorException(final Throwable t, final boolean executeCalledWithoutFuture,
            final boolean callableRequiresReturnValue) {
        if (executeCalledWithoutFuture) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
        }
        return t;
    }
}
