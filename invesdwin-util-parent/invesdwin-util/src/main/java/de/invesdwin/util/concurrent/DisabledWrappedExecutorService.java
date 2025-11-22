package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.ListenableFuture;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.shutdown.IShutdownHook;

@Immutable
public class DisabledWrappedExecutorService extends WrappedExecutorService {

    private volatile boolean failFast = false;

    public DisabledWrappedExecutorService(final ExecutorService delegate, final String name) {
        super(delegate, name);
        super.setDynamicThreadName(false);
        super.setFinalizerEnabled(false);
    }

    public DisabledWrappedExecutorService setFailFast(final boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    public boolean isFailFast() {
        return failFast;
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

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        //failFast not needed here, since it will anyhow ignore exceptions
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        //failFast not needed here, since it will anyhow ignore exceptions
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(final Runnable command) {
        if (failFast) {
            final ListenableFuture<?> future = super.submit(command);
            Futures.waitPropagatingNoInterrupt(future);
        } else {
            super.execute(command);
        }
    }

    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        if (failFast) {
            final ListenableFuture<T> future = super.submit(task);
            Futures.waitPropagatingNoInterrupt(future);
            return future;
        } else {
            return super.submit(task);
        }
    }

    @Override
    public ListenableFuture<?> submit(final Runnable task) {
        if (failFast) {
            final ListenableFuture<?> future = super.submit(task);
            Futures.waitPropagatingNoInterrupt(future);
            return future;
        } else {
            return super.submit(task);
        }
    }

    @Override
    public <T> ListenableFuture<T> submit(final Runnable task, final T result) {
        if (failFast) {
            final ListenableFuture<T> future = super.submit(task, result);
            Futures.waitPropagatingNoInterrupt(future);
            return future;
        } else {
            return super.submit(task, result);
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (failFast) {
            final List<Future<T>> futures = new ArrayList<>(tasks.size());
            for (final Callable<T> task : tasks) {
                final ListenableFuture<T> future = super.submit(task);
                Futures.waitPropagatingNoInterrupt(future);
                futures.add(future);
            }
            return futures;
        } else {
            return super.invokeAll(tasks);
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        if (failFast) {
            final List<Future<T>> futures = new ArrayList<>(tasks.size());
            for (final Callable<T> task : tasks) {
                final ListenableFuture<T> future = super.submit(task);
                Futures.waitPropagatingNoInterrupt(future);
                futures.add(future);
            }
            return futures;
        } else {
            return super.invokeAll(tasks);
        }
    }
}