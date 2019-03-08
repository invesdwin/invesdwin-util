package de.invesdwin.util.concurrent.nested;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@Immutable
public final class DisabledNestedExecutor implements INestedExecutor {

    public static final DisabledNestedExecutor INSTANCE = new DisabledNestedExecutor();

    private DisabledNestedExecutor() {}

    @Override
    public WrappedExecutorService getNestedExecutor() {
        return Executors.DISABLED_EXECUTOR;
    }

    @Override
    public WrappedExecutorService getNestedExecutor(final String suffix) {
        return Executors.DISABLED_EXECUTOR;
    }

    @Override
    public int getCurrentNestedThreadLevel() {
        return 0;
    }

}
