package de.invesdwin.util.error;

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
public final class FastIndexOutOfBoundsException extends IndexOutOfBoundsException {

    private static final FastIndexOutOfBoundsException INSTANCE = new FastIndexOutOfBoundsException(
            "Index out of bounds (Throwables.setDebugStackTraceEnabled(true) for more details)");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private FastIndexOutOfBoundsException(final String message) {
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

    public static IndexOutOfBoundsException maybeReplace(final IndexOutOfBoundsException e) {
        if (e instanceof FastIndexOutOfBoundsException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(e);
        }
    }

    public static IndexOutOfBoundsException maybeReplace(final IndexOutOfBoundsException e, final String message) {
        if (e instanceof FastIndexOutOfBoundsException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return getInstance(message, e);
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(message);
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final Object arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final int arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final int arg1, final int arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final int arg1, final int arg2,
            final int arg3) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg1, arg2, arg3));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final long arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final long arg1, final long arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final long arg1, final long arg2,
            final long arg3) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg1, arg2, arg3));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final Object arg1,
            final Object arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final Object... args) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastIndexOutOfBoundsException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static FastIndexOutOfBoundsException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static FastIndexOutOfBoundsException getInstance(final String message, final Throwable cause) {
        if (Throwables.isDebugStackTraceEnabled()) {
            final FastIndexOutOfBoundsException eof = new FastIndexOutOfBoundsException(message);
            if (cause != null) {
                eof.initCause(cause);
            }
            return eof;
        } else {
            return INSTANCE;
        }
    }

}
