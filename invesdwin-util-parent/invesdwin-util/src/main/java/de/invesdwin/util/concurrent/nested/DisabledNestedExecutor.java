package de.invesdwin.util.concurrent.nested;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@Immutable
public final class DisabledNestedExecutor implements INestedExecutor {

    public static final DisabledNestedExecutor INSTANCE = new DisabledNestedExecutor(
            DisabledNestedExecutor.class.getSimpleName() + "_INSTANCE");

    private final WrappedExecutorService executor;

    public DisabledNestedExecutor(final String name) {
        this.executor = Executors.newDisabledExecutor(name);
    }

    @Override
    public WrappedExecutorService getNestedExecutor() {
        return executor;
    }

    @Override
    public WrappedExecutorService getNestedExecutor(final String suffix) {
        return executor;
    }

    @Override
    public int getCurrentNestedThreadLevel() {
        return 0;
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

}
