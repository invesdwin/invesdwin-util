package de.invesdwin.util.collections.delegate.debug;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public class CloseableDelegateList<E> extends ADelegateList<E> implements Closeable {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CloseableDelegateList.class);

    private final CloseableDelegateListFinalizer<E> finalizer;

    public CloseableDelegateList(final TextDescription name, final List<E> delegate, final AtomicLong openReaders) {
        this.finalizer = new CloseableDelegateListFinalizer<E>(name);
        this.finalizer.register(this);
        this.finalizer.delegate = delegate;
        this.finalizer.openReaders = openReaders;
        openReaders.incrementAndGet();
    }

    @Override
    public void close() throws IOException {
        finalizer.close();
    }

    private static final class CloseableDelegateListFinalizer<E> extends AFinalizer {

        private final TextDescription name;
        private AtomicLong openReaders;
        private List<E> delegate;
        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();
        private Exception initStackTrace;
        private volatile boolean closed;

        private CloseableDelegateListFinalizer(final TextDescription name) {
            this.name = name;
            createInitStackTrace();
        }

        @Override
        protected void clean() {
            initStackTrace = null;
            closed = true;
            if (delegate instanceof Closeable) {
                Closeables.closeQuietly((Closeable) delegate);
            }
            openReaders.decrementAndGet();
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
            String warning = "Finalizing unclosed iterator [" + getClass().getName() + "]: " + name;
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

        @Override
        public boolean isThreadLocal() {
            return false;
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
