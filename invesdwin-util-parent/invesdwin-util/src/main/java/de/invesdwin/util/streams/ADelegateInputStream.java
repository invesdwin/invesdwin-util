package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.input.ClosedInputStream;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.cleanable.ACleanableAction;

@NotThreadSafe
public abstract class ADelegateInputStream extends InputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);
    private final WarningCleanableAction cleanable;

    public ADelegateInputStream() {
        this.cleanable = new WarningCleanableAction();
        if (cleanable.debugStackTraceEnabled) {
            cleanable.initStackTrace = new Exception();
            cleanable.initStackTrace.fillInStackTrace();
        }
        cleanable.register(this);
    }

    @Override
    public int available() throws IOException {
        return getDelegate().available();
    }

    protected InputStream getDelegate() {
        if (cleanable.delegate == null) {
            cleanable.delegate = newDelegate();
        }
        return cleanable.delegate;
    }

    protected abstract InputStream newDelegate();

    @Override
    public void close() throws IOException {
        cleanable.close();
    }

    public boolean isClosed() {
        return cleanable.isClosed();
    }

    @Override
    public synchronized void mark(final int readlimit) {
        getDelegate().mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return getDelegate().markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        getDelegate().reset();
    }

    @Override
    public int read() throws IOException {
        if (cleanable.debugStackTraceEnabled && cleanable.readStackTrace == null) {
            cleanable.initStackTrace = null;
            cleanable.readStackTrace = new Exception();
            cleanable.readStackTrace.fillInStackTrace();
        }
        final int read = getDelegate().read();
        if (shouldCloseOnMinus1Read() && read == -1) {
            close();
        }
        return read;
    }

    protected boolean shouldCloseOnMinus1Read() {
        return true;
    }

    private static final class WarningCleanableAction extends ACleanableAction {

        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();

        private InputStream delegate;

        private Exception initStackTrace;
        private Exception readStackTrace;

        @Override
        protected void clean() {
            if (delegate != null) {
                try {
                    delegate.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //forget the reference to original inputstream to potentially free some memory
            delegate = ClosedInputStream.CLOSED_INPUT_STREAM;
        }

        @Override
        protected void onRun() {
            if (delegate != null) {
                String warning = "Finalizing unclosed " + InputStream.class.getSimpleName() + " ["
                        + getClass().getName() + "]";
                if (debugStackTraceEnabled) {
                    final Exception stackTrace;
                    if (initStackTrace != null) {
                        warning += " which was initialized but never used";
                        stackTrace = initStackTrace;
                    } else {
                        stackTrace = readStackTrace;
                    }
                    if (stackTrace != null) {
                        warning += " from stacktrace:\n" + Throwables.getFullStackTrace(stackTrace);
                    }
                }
                LOGGER.warn(warning);
            }
        }

        @Override
        public boolean isClosed() {
            return delegate == ClosedInputStream.CLOSED_INPUT_STREAM;
        }

    }

}
