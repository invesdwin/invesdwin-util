package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class SimpleDelegateOutputStream extends OutputStream {

    private final OutputStream delegate;

    public SimpleDelegateOutputStream(final OutputStream delegate) {
        this.delegate = delegate;
    }

    public OutputStream getDelegate() {
        return delegate;
    }

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
