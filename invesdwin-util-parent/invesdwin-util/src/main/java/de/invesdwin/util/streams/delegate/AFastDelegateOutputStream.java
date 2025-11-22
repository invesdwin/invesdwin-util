package de.invesdwin.util.streams.delegate;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastDelegateOutputStream extends OutputStream {

    private OutputStream delegate;

    public AFastDelegateOutputStream() {}

    protected AFastDelegateOutputStream(final OutputStream delegate) {
        this.delegate = delegate;
    }

    public OutputStream getDelegate() {
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

    protected void onWrite() throws IOException {}

    @Override
    public void flush() throws IOException {
        getDelegate().flush();
    }

}
