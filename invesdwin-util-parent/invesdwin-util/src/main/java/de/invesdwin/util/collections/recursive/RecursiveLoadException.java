package de.invesdwin.util.collections.recursive;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RecursiveLoadException extends Exception implements IRecursiveLoadException {

    private static final long serialVersionUID = 1L;

    public RecursiveLoadException() {
        super();
    }

    public RecursiveLoadException(final String message) {
        super(message);
    }

    public RecursiveLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RecursiveLoadException(final Throwable cause) {
        super(cause);
    }

}
