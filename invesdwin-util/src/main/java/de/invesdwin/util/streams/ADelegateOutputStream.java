package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.output.ClosedOutputStream;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class ADelegateOutputStream extends OutputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);
    private final boolean debugStackTraceEnabled = Throwables.isDebugStackTraceEnabled();

    private OutputStream delegate;

    private Exception initStackTrace;
    private Exception readStackTrace;

    public ADelegateOutputStream() {
        if (debugStackTraceEnabled) {
            initStackTrace = new Exception();
            initStackTrace.fillInStackTrace();
        }
    }

    protected OutputStream getDelegate() {
        if (delegate == null) {
            delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract OutputStream newDelegate();

    @Override
    public void close() throws IOException {
        if (delegate != null) {
            delegate.close();
        }
        //forget the reference to original outputstream to potentially free some memory
        delegate = ClosedOutputStream.CLOSED_OUTPUT_STREAM;
    }

    /**
     * http://www.informit.com/articles/article.aspx?p=1216151&seqNum=7
     */
    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            if (delegate != null) {
                String warning = "Finalizing unclosed " + OutputStream.class.getSimpleName() + " ["
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
            close();
        }
        super.finalize();
    }

    public boolean isClosed() {
        return delegate == ClosedOutputStream.CLOSED_OUTPUT_STREAM;
    }

    @Override
    public void write(final int b) throws IOException {
        if (debugStackTraceEnabled && readStackTrace == null) {
            initStackTrace = null;
            readStackTrace = new Exception();
            readStackTrace.fillInStackTrace();
        }
        getDelegate().write(b);
    }

    @Override
    public void flush() throws IOException {
        getDelegate().flush();
    }

}
