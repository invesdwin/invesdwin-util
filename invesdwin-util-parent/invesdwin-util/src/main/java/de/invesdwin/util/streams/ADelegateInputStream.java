package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.input.ClosedInputStream;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public abstract class ADelegateInputStream extends InputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);
    private final DelegateInputStreamFinalizer finalizer;

    public ADelegateInputStream(final TextDescription name) {
        this.finalizer = new DelegateInputStreamFinalizer(name);
        this.finalizer.register(this);
    }

    @Override
    public int available() throws IOException {
        return getDelegate().available();
    }

    protected InputStream getDelegate() {
        if (finalizer.delegate == null) {
            finalizer.delegate = newDelegate();
        }
        return finalizer.delegate;
    }

    protected abstract InputStream newDelegate();

    @Override
    public void close() throws IOException {
        super.close();
        finalizer.close();
    }

    public boolean isClosed() {
        return finalizer.isClosed();
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
        onRead();
        return getDelegate().read();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        onRead();
        return getDelegate().read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        onRead();
        return getDelegate().read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        onRead();
        return getDelegate().readAllBytes();
    }

    @Override
    public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
        onRead();
        return getDelegate().readNBytes(b, off, len);
    }

    @Override
    public byte[] readNBytes(final int len) throws IOException {
        onRead();
        return getDelegate().readNBytes(len);
    }

    protected void onRead() {
        if (finalizer.debugStackTraceEnabled && finalizer.readStackTrace == null) {
            finalizer.initStackTrace = null;
            finalizer.readStackTrace = new Exception();
            finalizer.readStackTrace.fillInStackTrace();
        }
    }

    private static final class DelegateInputStreamFinalizer extends AFinalizer {

        private final TextDescription name;
        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();

        private InputStream delegate;

        private Exception initStackTrace;
        private Exception readStackTrace;

        private DelegateInputStreamFinalizer(final TextDescription name) {
            this.name = name;
            if (debugStackTraceEnabled) {
                initStackTrace = new Exception();
                initStackTrace.fillInStackTrace();
            }
        }

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
                        + delegate.getClass().getName() + "]: " + name;
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
        protected boolean isCleaned() {
            return delegate == ClosedInputStream.CLOSED_INPUT_STREAM;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

}
