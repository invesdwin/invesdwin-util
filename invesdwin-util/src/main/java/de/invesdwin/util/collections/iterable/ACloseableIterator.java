package de.invesdwin.util.collections.iterable;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class ACloseableIterator<E> implements Iterator<E>, Closeable {

    private static boolean finalizerDebugEnabled;

    private boolean closed;

    private Exception stackTrace;

    /**
     * http://www.informit.com/articles/article.aspx?p=1216151&seqNum=7
     */
    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            if (finalizerDebugEnabled) {
                String warning = "Finalizing unclosed iterator [" + getClass().getName() + "]";
                if (stackTrace != null) {
                    warning += " from stacktrace:\n" + Throwables.getFullStackTrace(stackTrace);
                }
                System.err.println(warning); //SUPPRESS CHECKSTYLE single line
            }
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
            throw new NoSuchElementException("closed");
        }
        if (isFinalizerDebugEnabled() && stackTrace == null) {
            stackTrace = new Exception();
            stackTrace.fillInStackTrace();
        }
        final E next;
        try {
            next = innerNext();
        } catch (final NoSuchElementException e) {
            close();
            throw e;
        }
        if (next == null) {
            close();
        }
        return next;
    }

    protected abstract E innerNext();

    @Override
    public final void remove() {
        if (isClosed()) {
            throw new NoSuchElementException("closed");
        }
        innerRemove();
    }

    protected void innerRemove() {}

    @Override
    public final void close() {
        if (!isClosed()) {
            closed = true;
            stackTrace = null;
            innerClose();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    protected void innerClose() {
        throw new UnsupportedOperationException();
    }

    public static void setFinalizerDebugEnabled(final boolean finalizerDebugEnabled) {
        ACloseableIterator.finalizerDebugEnabled = finalizerDebugEnabled;
    }

    public static boolean isFinalizerDebugEnabled() {
        return finalizerDebugEnabled;
    }

}
