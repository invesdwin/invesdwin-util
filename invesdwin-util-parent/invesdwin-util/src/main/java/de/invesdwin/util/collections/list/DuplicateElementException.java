package de.invesdwin.util.collections.list;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

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
public class DuplicateElementException extends NoSuchElementException {

    private static final DuplicateElementException INSTANCE = new DuplicateElementException("duplicate");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    public DuplicateElementException(final String message) {
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

    public static DuplicateElementException maybeReplace(final NoSuchElementException e, final String message) {
        if (e instanceof DuplicateElementException || Throwables.isDebugStackTraceEnabled()) {
            return (DuplicateElementException) e;
        } else {
            return new DuplicateElementException(message + ": " + e.toString());
        }
    }

    public static DuplicateElementException getInstance() {
        if (Throwables.isDebugStackTraceEnabled()) {
            throw new DuplicateElementException("duplicate");
        } else {
            throw INSTANCE;
        }
    }

}
