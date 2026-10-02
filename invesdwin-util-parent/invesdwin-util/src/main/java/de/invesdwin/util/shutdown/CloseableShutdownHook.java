package de.invesdwin.util.shutdown;

import java.io.Closeable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class CloseableShutdownHook implements IShutdownHook {

    private final Closeable closeable;

    public CloseableShutdownHook(final Closeable closeable) {
        this.closeable = closeable;
    }

    @Override
    public void shutdown() throws Exception {
        closeable.close();
    }

}
