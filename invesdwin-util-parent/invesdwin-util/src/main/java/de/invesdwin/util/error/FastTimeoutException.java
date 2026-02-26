package de.invesdwin.util.error;

import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.description.TextDescription;

/**
 * Often it is faster to use iterators without calling hasNext and instead catching the NoSuchElementException. Throw
 * this exception instead to skip the stack trace generation to make it even faster.
 * 
 * Though better provide an adequate reason where and why it was thrown so that debugging is a bit easier.
 * 
 * Anyhow, stacktraces can be enabled for this exception via Throwables.setDebugStackTraceEnabled(true).
 * 
 */
@NotThreadSafe
public final class FastTimeoutException extends TimeoutException {

    private static final FastTimeoutException INSTANCE = new FastTimeoutException(
            "timeout exceeded (Throwables.setDebugStackTraceEnabled(true) for more details)");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastTimeoutException(final String message) {
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

    public static TimeoutException maybeReplace(final TimeoutException e) {
        if (e instanceof FastTimeoutException || isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(e);
        }
    }

    public static TimeoutException maybeReplace(final TimeoutException e, final String message) {
        if (e instanceof FastTimeoutException || isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(message, e);
        }
    }

    public static FastTimeoutException getInstance(final String message) {
        if (isDebugStackTraceEnabled()) {
            return new FastTimeoutException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastTimeoutException getInstance(final String message, final Object arg) {
        if (isDebugStackTraceEnabled()) {
            return new FastTimeoutException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastTimeoutException getInstance(final String message, final Object arg1, final Object arg2) {
        if (isDebugStackTraceEnabled()) {
            return new FastTimeoutException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastTimeoutException getInstance(final String message, final Object... args) {
        if (isDebugStackTraceEnabled()) {
            return new FastTimeoutException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static FastTimeoutException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastTimeoutException getInstance(final String message, final Throwable cause) {
        if (isDebugStackTraceEnabled()) {
            final FastTimeoutException timeout = new FastTimeoutException(message);
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
