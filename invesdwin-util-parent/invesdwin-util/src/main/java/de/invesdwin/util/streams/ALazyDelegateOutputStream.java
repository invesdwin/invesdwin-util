package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ALazyDelegateOutputStream extends OutputStream {

    private OutputStream delegate;

    public ALazyDelegateOutputStream() {
    }

    public final OutputStream getDelegate() {
        if (delegate == null) {
            this.delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract OutputStream newDelegate();

    @Override
    public void close() throws IOException {
        getDelegate().close();
    }

    @Override
    public void write(final int b) throws IOException {
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        getDelegate().write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        getDelegate().flush();
    }

}
