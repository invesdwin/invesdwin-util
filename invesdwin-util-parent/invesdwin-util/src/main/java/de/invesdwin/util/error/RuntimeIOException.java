package de.invesdwin.util.error;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RuntimeIOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimeIOException(final String message) {
        super(message);
    }

    public RuntimeIOException(final Throwable cause) {
        super(cause);
    }

    public RuntimeIOException(final String message, final Throwable cause) {
        super(message, cause);
    }

}