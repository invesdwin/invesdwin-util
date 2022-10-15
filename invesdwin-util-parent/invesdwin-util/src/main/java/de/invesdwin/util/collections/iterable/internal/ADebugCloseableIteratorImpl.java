package de.invesdwin.util.collections.iterable.internal;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public abstract class ADebugCloseableIteratorImpl<E> implements ICloseableIteratorImpl<E> {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ADebugCloseableIteratorImpl.class);

    private final DebugCloseableIteratorFinalizer finalizer;

    public ADebugCloseableIteratorImpl(final TextDescription name, final String className) {
        this.finalizer = new DebugCloseableIteratorFinalizer(name, className);
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
            throw FastNoSuchElementException.getInstance("ACloseableIterator: next blocked because already closed");
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
            throw new NullPointerException("ACloseableIterator: next is null");
        }
        return next;
    }

    protected abstract E innerNext();

    @Override
    public final void remove() {
        if (isClosed()) {
            throw FastNoSuchElementException.getInstance("ACloseableIterator: remove blocked because already closed");
        }
        innerRemove();
    }

    protected abstract void innerRemove();

    @Override
    public void close() {
        finalizer.close();
    }

    @Override
    public boolean isClosed() {
        return finalizer.isClosed();
    }

    private static final class DebugCloseableIteratorFinalizer extends AFinalizer {

        private final TextDescription name;
        private final String className;
        private Exception initStackTrace;
        private Exception nextOrHasNextStackTrace;
        private volatile boolean closed;

        private DebugCloseableIteratorFinalizer(final TextDescription name, final String className) {
            this.name = name;
            this.className = className;
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
            initStackTrace = new Exception();
            initStackTrace.fillInStackTrace();
        }

        private void createNextOrHasNextStackTrace() {
            if (nextOrHasNextStackTrace == null) {
                initStackTrace = null;
                nextOrHasNextStackTrace = new Exception();
                nextOrHasNextStackTrace.fillInStackTrace();
            }
        }

        private void createUnclosedFinalizeMessageLog() {
            String warning = "Finalizing unclosed iterator [" + className + "]: " + name;
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
            LOGGER.warn(warning);
        }

        @Override
        protected boolean isCleaned() {
            return closed;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

}
