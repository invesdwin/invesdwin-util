package de.invesdwin.util.streams.delegate;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class NonClosingDelegateOutputStream extends OutputStream {

    private final OutputStream delegate;

    public NonClosingDelegateOutputStream(final OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(final byte[] b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }
}
