package de.invesdwin.util.concurrent.nested;

import de.invesdwin.util.concurrent.WrappedExecutorService;

public interface INestedExecutor {

    WrappedExecutorService getNestedExecutor();

    WrappedExecutorService getNestedExecutor(String suffix);

    int getCurrentNestedThreadLevel();

}
