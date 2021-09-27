package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class NonClosingDelegateInputStream extends InputStream {

    private final InputStream delegate;

    public NonClosingDelegateInputStream(final InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void mark(final int readlimit) {
        delegate.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return delegate.read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return delegate.read(b, off, len);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return delegate.skip(n);
    }

}
