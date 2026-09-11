package de.invesdwin.util.shutdown;

import java.io.Closeable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class CloseableShutdownHookThread extends Thread {

    private final Closeable closeable;

    public CloseableShutdownHookThread(final Closeable closeable) {
        this.closeable = closeable;
    }

    @Override
    public void run() {
        try {
            closeable.close();
        } catch (final Exception e) {
            throw new RuntimeException("Error while closing closeable in shutdown hook", e);
        }
    }

}
