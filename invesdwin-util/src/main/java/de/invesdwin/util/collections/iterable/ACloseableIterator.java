package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class ACloseableIterator<E> implements ICloseableIterator<E> {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);

    private boolean closed;

    private Exception initStackTrace;
    private Exception nextStackTrace;

    public ACloseableIterator() {
        createInitStackTrace();
    }

    protected void createInitStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            initStackTrace = new Exception();
            initStackTrace.fillInStackTrace();
        }
    }

    protected void createNextStackTrace() {
        if (Throwables.isDebugStackTraceEnabled() && nextStackTrace == null) {
            initStackTrace = null;
            nextStackTrace = new Exception();
            nextStackTrace.fillInStackTrace();
        }
    }

    protected void createUnclosedFinalizeMessageLog() {
        String warning = "Finalizing unclosed iterator [" + getClass().getName() + "]";
        if (Throwables.isDebugStackTraceEnabled()) {
            final Exception stackTrace;
            if (initStackTrace != null) {
                warning += " which was initialized but never used";
                stackTrace = initStackTrace;
            } else {
                stackTrace = nextStackTrace;
            }
            if (stackTrace != null) {
                warning += " from stacktrace:\n" + Throwables.getFullStackTrace(stackTrace);
            }
        }
        LOGGER.warn(warning);
    }

    /**
     * http://www.informit.com/articles/article.aspx?p=1216151&seqNum=7
     */
    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            createUnclosedFinalizeMessageLog();
            close();
        }
        super.finalize();
    }

    @Override
    public final boolean hasNext() {
        if (isClosed()) {
            return false;
        }
        final boolean hasNext = innerHasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    protected abstract boolean innerHasNext();

    @Override
    public final E next() {
        if (isClosed()) {
            throw new FastNoSuchElementException("ACloseableIterator: next blocked because already closed");
        }
        createNextStackTrace();
        final E next;
        try {
            next = innerNext();
        } catch (final NoSuchElementException e) {
            close();
            throw FastNoSuchElementException.maybeReplace(e, "ACloseableIterator: innerNext threw");
        }
        if (next == null) {
            close();
            throw new FastNoSuchElementException("ACloseableIterator: next is null");
        }
        return next;
    }

    protected abstract E innerNext();

    @Override
    public final void remove() {
        if (isClosed()) {
            throw new FastNoSuchElementException("ACloseableIterator: remove blocked because already closed");
        }
        innerRemove();
    }

    protected void innerRemove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void close() {
        if (!isClosed()) {
            closed = true;
            initStackTrace = null;
            nextStackTrace = null;
            innerClose();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    protected abstract void innerClose();

}
