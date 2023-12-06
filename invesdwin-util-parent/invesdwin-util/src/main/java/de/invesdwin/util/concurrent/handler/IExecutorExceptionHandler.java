package de.invesdwin.util.concurrent.handler;

public interface IExecutorExceptionHandler {

    /**
     * Return null to prevent rethrowing.
     */
    Throwable handleExecutorException(Throwable t, boolean executeCalledWithoutFuture,
            boolean callableRequiresReturnValue);

}
