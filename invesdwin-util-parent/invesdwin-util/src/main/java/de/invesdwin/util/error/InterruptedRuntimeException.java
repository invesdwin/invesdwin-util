package de.invesdwin.util.error;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class InterruptedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InterruptedRuntimeException() {
        super(new InterruptedException());
    }

    public InterruptedRuntimeException(final String message) {
        super(message, new InterruptedException(message));
    }

    public InterruptedRuntimeException(final String message, final Throwable cause) {
        super(message, new InterruptedException(message));
        getCause().initCause(cause);
    }

    public InterruptedRuntimeException(final Throwable cause) {
        super(new InterruptedException());
        getCause().initCause(cause);
    }

}
