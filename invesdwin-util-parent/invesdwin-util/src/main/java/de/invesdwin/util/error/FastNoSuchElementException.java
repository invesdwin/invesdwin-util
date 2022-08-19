package de.invesdwin.util.error;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

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
public final class FastNoSuchElementException extends NoSuchElementException {

    private static final FastNoSuchElementException INSTANCE = new FastNoSuchElementException("end reached");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastNoSuchElementException(final String message) {
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

    public static NoSuchElementException maybeReplace(final NoSuchElementException e) {
        if (e instanceof FastNoSuchElementException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(e);
        }
    }

    public static NoSuchElementException maybeReplace(final NoSuchElementException e, final String message) {
        if (e instanceof FastNoSuchElementException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(message, e);
        }
    }

    public static FastNoSuchElementException getInstance(final String message) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastNoSuchElementException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastNoSuchElementException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastNoSuchElementException getInstance(final String message, final Throwable cause) {
        if (Throwables.isDebugStackTraceEnabled()) {
            final FastNoSuchElementException eof = new FastNoSuchElementException(message);
            if (cause != null) {
                eof.initCause(cause);
            }
            return eof;
        } else {
            return INSTANCE;
        }
    }

}
