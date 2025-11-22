package de.invesdwin.util.collections.recursive;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RecursiveLoadRuntimeException extends RuntimeException implements IRecursiveLoadException {

    private static final long serialVersionUID = 1L;

    public RecursiveLoadRuntimeException() {
        super();
    }

    public RecursiveLoadRuntimeException(final String message) {
        super(message);
    }

    public RecursiveLoadRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RecursiveLoadRuntimeException(final Throwable cause) {
        super(cause);
    }

}
