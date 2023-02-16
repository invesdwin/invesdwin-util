package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BroadcastingOutputStream extends OutputStream {

    private final OutputStream[] delegates;

    public BroadcastingOutputStream(final OutputStream... delegates) {
        this.delegates = delegates;
    }

    public OutputStream[] getDelegates() {
        return delegates;
    }

    @Override
    public void close() throws IOException {
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].close();
        }
    }

    @Override
    public void write(final int b) throws IOException {
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].write(b);
        }
    }

    @Override
    public void write(final byte[] b) throws IOException {
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].write(b);
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].flush();
        }
    }

}
