package de.invesdwin.util.collections.loadingcache.historical.query.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class ResetCacheException extends Exception {

    private static final long serialVersionUID = 1L;

    public ResetCacheException() {
        super();
    }

    public ResetCacheException(final String message) {
        super(message);
    }

    public ResetCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResetCacheException(final Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        } else {
            return this; // no stack trace for performance
        }
    }

}
