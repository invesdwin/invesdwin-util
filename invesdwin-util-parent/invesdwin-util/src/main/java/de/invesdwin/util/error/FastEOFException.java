package de.invesdwin.util.error;

import java.io.EOFException;

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
public final class FastEOFException extends EOFException {

    private static final FastEOFException INSTANCE = new FastEOFException("end reached");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastEOFException(final String message) {
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

    public static EOFException maybeReplace(final EOFException e) {
        if (e instanceof FastEOFException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(e);
        }
    }

    public static EOFException maybeReplace(final EOFException e, final String message) {
        if (e instanceof FastEOFException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(message, e);
        }
    }

    public static FastEOFException getInstance(final String message) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastEOFException getInstance(final String message, final Object arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastEOFException getInstance(final String message, final Object arg1, final Object arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastEOFException getInstance(final String message, final Object... args) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static FastEOFException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastEOFException getInstance(final String message, final Throwable cause) {
        if (Throwables.isDebugStackTraceEnabled()) {
            final FastEOFException eof = new FastEOFException(message);
            if (cause != null) {
                eof.initCause(cause);
            }
            return eof;
        } else {
            return INSTANCE;
        }
    }

}
