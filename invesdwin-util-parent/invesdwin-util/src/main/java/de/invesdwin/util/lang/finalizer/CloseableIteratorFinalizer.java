package de.invesdwin.util.lang.finalizer;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class CloseableIteratorFinalizer extends AFinalizer {

    private Closeable closeable;

    public CloseableIteratorFinalizer(final Closeable closeable) {
        this.closeable = closeable;
    }

    @Override
    protected void clean() {
        try {
            closeable.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        closeable = null;
    }

    @Override
    public boolean isClosed() {
        return closeable == null;
    }

}
