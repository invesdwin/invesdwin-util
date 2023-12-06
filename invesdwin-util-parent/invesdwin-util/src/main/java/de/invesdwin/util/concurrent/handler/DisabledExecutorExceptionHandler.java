package de.invesdwin.util.concurrent.handler;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledExecutorExceptionHandler implements IExecutorExceptionHandler {

    public static final DisabledExecutorExceptionHandler INSTANCE = new DisabledExecutorExceptionHandler();

    private DisabledExecutorExceptionHandler() {}

    @Override
    public Throwable handleExecutorException(final Throwable t, final boolean executeCalledWithoutFuture,
            final boolean callableRequiresReturnValue) {
        return t;
    }

}
