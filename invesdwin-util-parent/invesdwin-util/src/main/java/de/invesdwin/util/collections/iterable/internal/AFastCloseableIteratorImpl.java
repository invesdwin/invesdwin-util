package de.invesdwin.util.collections.iterable.internal;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public abstract class AFastCloseableIteratorImpl<E> implements ICloseableIteratorImpl<E> {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AFastCloseableIteratorImpl.class);

    private final FastCloseableIteratorFinalizer finalizer;

    public AFastCloseableIteratorImpl(final TextDescription name, final String className) {
        this.finalizer = new FastCloseableIteratorFinalizer(name, className);
        this.finalizer.register(this);
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
            throw FastNoSuchElementException.getInstance("ACloseableIterator: next blocked because already closed");
        }
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

    private static final class FastCloseableIteratorFinalizer extends AFinalizer {

        private final TextDescription name;
        private final String className;
        private volatile boolean closed;

        private FastCloseableIteratorFinalizer(final TextDescription name, final String className) {
            this.name = name;
            this.className = className;
        }

        @Override
        protected void clean() {
            closed = true;
        }

        @Deprecated
        @Override
        public void onRun() {
            createUnclosedFinalizeMessageLog();
        }

        private void createUnclosedFinalizeMessageLog() {
            final String warning = "Finalizing unclosed iterator [" + className + "]: " + name;
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
