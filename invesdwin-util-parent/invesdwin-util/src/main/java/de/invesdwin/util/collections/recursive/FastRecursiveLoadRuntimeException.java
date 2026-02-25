package de.invesdwin.util.collections.recursive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public final class FastRecursiveLoadRuntimeException extends RecursiveLoadRuntimeException {

    private static final FastRecursiveLoadRuntimeException INSTANCE = new FastRecursiveLoadRuntimeException(
            "recursive load (Throwables.setDebugStackTraceEnabled(true) for more details)");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastRecursiveLoadRuntimeException(final String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        } else {
            return this; // no stack trace for performance
        }
    }

    public static FastRecursiveLoadRuntimeException getInstance(final String message) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadRuntimeException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadRuntimeException getInstance(final String message, final Object arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadRuntimeException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadRuntimeException getInstance(final String message, final Object arg1,
            final Object arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadRuntimeException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadRuntimeException getInstance(final String message, final Object... args) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadRuntimeException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadRuntimeException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastRecursiveLoadRuntimeException getInstance(final String message, final Throwable cause) {
        if (Throwables.isDebugStackTraceEnabled()) {
            final FastRecursiveLoadRuntimeException timeout = new FastRecursiveLoadRuntimeException(message);
            if (cause != null) {
                timeout.initCause(cause);
            }
            return timeout;
        } else {
            return INSTANCE;
        }
    }

}
