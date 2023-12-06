package de.invesdwin.util.concurrent.handler;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AlwaysUncaughtExecutorExceptionHandler implements IExecutorExceptionHandler {

    public static final AlwaysUncaughtExecutorExceptionHandler INSTANCE = new AlwaysUncaughtExecutorExceptionHandler();

    private AlwaysUncaughtExecutorExceptionHandler() {}

    @Override
    public Throwable handleExecutorException(final Throwable t, final boolean executeCalledWithoutFuture,
            final boolean callableRequiresReturnValue) {
        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
        return t;
    }
}
