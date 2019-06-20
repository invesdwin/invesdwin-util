package de.invesdwin.util.collections.delegate.debug;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public class CloseableDelegateList<E> extends ADelegateList<E> implements Closeable {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CloseableDelegateList.class);

    private final CloseableDelegateListFinalizer<E> finalizer;

    public CloseableDelegateList(final List<E> delegate) {
        this.finalizer = new CloseableDelegateListFinalizer<E>();
        this.finalizer.register(this);
        this.finalizer.delegate = delegate;
    }

    @Override
    public void close() throws IOException {
        finalizer.close();
    }

    private static final class CloseableDelegateListFinalizer<E> extends AFinalizer {

        private List<E> delegate;
        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();
        private Exception initStackTrace;
        private volatile boolean closed;

        private CloseableDelegateListFinalizer() {
            createInitStackTrace();
        }

        @Override
        protected void clean() {
            initStackTrace = null;
            closed = true;
            if (delegate instanceof Closeable) {
                Closeables.closeQuietly((Closeable) delegate);
            }
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

        private void createUnclosedFinalizeMessageLog() {
            String warning = "Finalizing unclosed iterator [" + getClass().getName() + "]";
            if (debugStackTraceEnabled) {
                if (initStackTrace != null) {
                    warning += " from stacktrace:\n" + Throwables.getFullStackTrace(initStackTrace);
                }
            }
            LOGGER.warn(warning);
        }

        @Override
        protected boolean isCleaned() {
            return closed;
        }

    }

    @Override
    protected List<E> newDelegate() {
        return null;
    }

    @Override
    public List<E> getDelegate() {
        return finalizer.delegate;
    }

}
