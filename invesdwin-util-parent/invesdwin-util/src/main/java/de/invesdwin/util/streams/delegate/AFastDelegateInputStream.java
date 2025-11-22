package de.invesdwin.util.streams.delegate;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastDelegateInputStream extends InputStream {

    private InputStream delegate;

    public AFastDelegateInputStream() {}

    protected AFastDelegateInputStream(final InputStream delegate) {
        this.delegate = delegate;
    }

    public InputStream getDelegate() {
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

    protected void onRead() throws IOException {}

}
