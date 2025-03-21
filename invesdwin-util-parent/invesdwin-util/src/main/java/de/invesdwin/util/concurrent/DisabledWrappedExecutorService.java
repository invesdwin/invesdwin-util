package de.invesdwin.util.concurrent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.shutdown.IShutdownHook;

@Immutable
public class DisabledWrappedExecutorService extends WrappedExecutorService {
    public DisabledWrappedExecutorService(final ExecutorService delegate, final String name) {
        super(delegate, name);
        super.setDynamicThreadName(false);
        super.setFinalizerEnabled(false);
    }

    @Override
    protected void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        //noop
    }

    @Override
    protected void decrementPendingCount() {
        //noop
    }

    @Override
    protected void notifyPendingCountListeners(final int currentPendingCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IShutdownHook newShutdownHook(final ExecutorService delegate) {
        //shutdown hook disabled
        return null;
    }

    @Override
    public void shutdown() {
        //noop
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean awaitTermination() throws InterruptedException {
        return true;
    }

    @Override
    public boolean awaitTermination(final java.time.Duration timeout) throws InterruptedException {
        return true;
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public WrappedExecutorService setDynamicThreadName(final boolean dynamicThreadName) {
        //disabled
        return this;
    }
}