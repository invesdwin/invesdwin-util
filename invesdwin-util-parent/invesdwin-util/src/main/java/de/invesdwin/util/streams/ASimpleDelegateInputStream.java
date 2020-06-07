package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ASimpleDelegateInputStream extends InputStream {

    private final InputStream delegate;

    public ASimpleDelegateInputStream(final InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
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
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

}
