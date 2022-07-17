package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ALazyDelegateInputStream extends InputStream {

    private InputStream delegate;

    public ALazyDelegateInputStream() {
    }

    public final InputStream getDelegate() {
        if (delegate == null) {
            this.delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract InputStream newDelegate();

    @Override
    public int available() throws IOException {
        return getDelegate().available();
    }

    @Override
    public void close() throws IOException {
        getDelegate().close();
    }

    @Override
    public void mark(final int readlimit) {
        getDelegate().mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return getDelegate().markSupported();
    }

    @Override
    public void reset() throws IOException {
        getDelegate().reset();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return getDelegate().read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return getDelegate().read(b, off, len);
    }

    @Override
    public int read() throws IOException {
        return getDelegate().read();
    }

}
