package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.output.ClosedOutputStream;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.description.TextDescription;

@NotThreadSafe
public abstract class ADelegateOutputStream extends OutputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);

    private final DelegateOutputStreamFinalizer finalizer;

    public ADelegateOutputStream(final TextDescription name) {
        this.finalizer = new DelegateOutputStreamFinalizer(name);
        this.finalizer.register(this);
    }

    protected OutputStream getDelegate() {
        if (finalizer.delegate == null) {
            finalizer.delegate = newDelegate();
        }
        return finalizer.delegate;
    }

    protected abstract OutputStream newDelegate();

    @Override
    public void close() throws IOException {
        super.close();
        finalizer.close();
    }

    public boolean isClosed() {
        return finalizer.isClosed();
    }

    @Override
    public void write(final int b) throws IOException {
        onWrite();
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        onWrite();
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        onWrite();
        getDelegate().write(b, off, len);
    }

    private void onWrite() {
        if (finalizer.debugStackTraceEnabled && finalizer.readStackTrace == null) {
            finalizer.initStackTrace = null;
            finalizer.readStackTrace = new Exception();
            finalizer.readStackTrace.fillInStackTrace();
        }
    }

    @Override
    public void flush() throws IOException {
        getDelegate().flush();
    }

    private static final class DelegateOutputStreamFinalizer extends AFinalizer {

        private final TextDescription name;
        private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();

        private OutputStream delegate;

        private Exception initStackTrace;
        private Exception readStackTrace;

        private DelegateOutputStreamFinalizer(final TextDescription name) {
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
            //forget the reference to original outputstream to potentially free some memory
            delegate = ClosedOutputStream.CLOSED_OUTPUT_STREAM;
        }

        @Override
        protected void onRun() {
            if (delegate != null) {
                String warning = "Finalizing unclosed " + OutputStream.class.getSimpleName() + " ["
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
            return delegate == ClosedOutputStream.CLOSED_OUTPUT_STREAM;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }
}
