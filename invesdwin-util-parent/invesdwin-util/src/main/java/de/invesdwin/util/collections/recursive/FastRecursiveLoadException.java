package de.invesdwin.util.collections.recursive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public final class FastRecursiveLoadException extends RecursiveLoadException {

    private static final FastRecursiveLoadException INSTANCE = new FastRecursiveLoadException(
            "recursive load (Throwables.setDebugStackTraceEnabled(true) for more details)");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastRecursiveLoadException(final String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        } else {
            return this; // no stack trace for performance
        }
    }

    public static FastRecursiveLoadException getInstance(final String message) {
        if (isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadException getInstance(final String message, final Object arg) {
        if (isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadException getInstance(final String message, final Object arg1, final Object arg2) {
        if (isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadException getInstance(final String message, final Object... args) {
        if (isDebugStackTraceEnabled()) {
            return new FastRecursiveLoadException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static FastRecursiveLoadException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastRecursiveLoadException getInstance(final String message, final Throwable cause) {
        if (isDebugStackTraceEnabled()) {
            final FastRecursiveLoadException timeout = new FastRecursiveLoadException(message);
            if (cause != null) {
                timeout.initCause(cause);
            }
            return timeout;
        } else {
            return INSTANCE;
        }
    }

    private static boolean isDebugStackTraceEnabled() {
        return Throwables.isDebugStackTraceEnabled();
    }
}
