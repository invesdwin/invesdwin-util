package de.invesdwin.util.concurrent.nested;

import java.io.Closeable;

import de.invesdwin.util.concurrent.WrappedExecutorService;

public interface INestedExecutor extends Closeable {

    WrappedExecutorService getNestedExecutor();

    WrappedExecutorService getNestedExecutor(String suffix);

    int getCurrentNestedThreadLevel();

    @Override
    void close();

}
