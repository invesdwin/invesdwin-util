package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.input.ClosedInputStream;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class ADelegateInputStream extends InputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ACloseableIterator.class);

    private InputStream delegate;

    private Exception initStackTrace;
    private Exception readStackTrace;

    public ADelegateInputStream() {
        if (Throwables.isDebugStackTraceEnabled()) {
            initStackTrace = new Exception();
            initStackTrace.fillInStackTrace();
        }
    }

    @Override
    public int available() throws IOException {
        return getDelegate().available();
    }

    protected InputStream getDelegate() {
        if (delegate == null) {
            delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract InputStream newDelegate();

    @Override
    public void close() throws IOException {
        if (delegate != null) {
            delegate.close();
        }
        //forget the reference to original inputstream to potentially free some memory
        delegate = ClosedInputStream.CLOSED_INPUT_STREAM;
    }

    /**
     * http://www.informit.com/articles/article.aspx?p=1216151&seqNum=7
     */
    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            if (delegate != null) {
                String warning = "Finalizing unclosed " + InputStream.class.getSimpleName() + " ["
                        + getClass().getName() + "]";
                if (Throwables.isDebugStackTraceEnabled()) {
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
        return delegate == ClosedInputStream.CLOSED_INPUT_STREAM;
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
        if (Throwables.isDebugStackTraceEnabled() && readStackTrace == null) {
            initStackTrace = null;
            readStackTrace = new Exception();
            readStackTrace.fillInStackTrace();
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

}
