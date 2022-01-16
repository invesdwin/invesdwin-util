package de.invesdwin.util.error;

import java.io.EOFException;

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
public class FastEOFException extends EOFException {

    private static final FastEOFException INSTANCE = new FastEOFException("end reached");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    public FastEOFException(final String message) {
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

    public static EOFException maybeReplace(final EOFException e, final String message) {
        if (e instanceof FastEOFException || Throwables.isDebugStackTraceEnabled()) {
            return e;
        } else {
            return new FastEOFException(message + ": " + e.toString());
        }
    }

    public static FastEOFException getInstance() throws FastEOFException {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException("end reached");
        } else {
            return INSTANCE;
        }
    }

}
