package de.invesdwin.util.concurrent.nested;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.DisabledWrappedExecutorService;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@Immutable
public final class DisabledNestedExecutor implements INestedExecutor {

    public static final DisabledNestedExecutor INSTANCE = new DisabledNestedExecutor(
            DisabledNestedExecutor.class.getSimpleName() + "_INSTANCE");

    private final DisabledWrappedExecutorService executor;

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

    public DisabledNestedExecutor setFailFast(final boolean failFast) {
        executor.setFailFast(failFast);
        return this;
    }

    public boolean isFailFast() {
        return executor.isFailFast();
    }

}
