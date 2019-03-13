package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public abstract class ACloseableIterator<E> implements ICloseableIterator<E> {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);

    private final CloseableIteratorFinalizer finalizer;

    public ACloseableIterator() {
        this.finalizer = new CloseableIteratorFinalizer();
        this.finalizer.register(this);
    }

    @Override
    public final boolean hasNext() {
        if (isClosed()) {
            return false;
        }
        finalizer.createNextOrHasNextStackTrace();
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
        finalizer.createNextOrHasNextStackTrace();
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
        finalizer.close();
    }

    public boolean isClosed() {
        return finalizer.isClosed();
    }

    private static final class CloseableIteratorFinalizer extends AFinalizer {

        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();
        private Exception initStackTrace;
        private Exception nextOrHasNextStackTrace;
        private volatile boolean closed;

        private CloseableIteratorFinalizer() {
            createInitStackTrace();
        }

        @Override
        protected void clean() {
            initStackTrace = null;
            nextOrHasNextStackTrace = null;
            closed = true;
        }

        @Deprecated
        @Override
        public void onRun() {
            createUnclosedFinalizeMessageLog();
        }

        private void createInitStackTrace() {
            if (debugStackTraceEnabled) {
                initStackTrace = new Exception();
                initStackTrace.fillInStackTrace();
            }
        }

        private void createNextOrHasNextStackTrace() {
            if (debugStackTraceEnabled && nextOrHasNextStackTrace == null) {
                initStackTrace = null;
                nextOrHasNextStackTrace = new Exception();
                nextOrHasNextStackTrace.fillInStackTrace();
            }
        }

        private void createUnclosedFinalizeMessageLog() {
            String warning = "Finalizing unclosed iterator [" + getClass().getName() + "]";
            if (debugStackTraceEnabled) {
                final Exception stackTrace;
                if (initStackTrace != null) {
                    warning += " which was initialized but never used";
                    stackTrace = initStackTrace;
                } else {
                    stackTrace = nextOrHasNextStackTrace;
                }
                if (stackTrace != null) {
                    warning += " from stacktrace:\n" + Throwables.getFullStackTrace(stackTrace);
                }
            }
            LOGGER.warn(warning);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

    }

}
