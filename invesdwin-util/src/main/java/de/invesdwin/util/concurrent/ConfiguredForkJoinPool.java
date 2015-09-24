package de.invesdwin.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;

@ThreadSafe
public class ConfiguredForkJoinPool extends ForkJoinPool {

    private final IShutdownHook shutdownHook = new IShutdownHook() {
        @Override
        public void shutdown() throws Exception {
            shutdownNow();
        }
    };

    public ConfiguredForkJoinPool(@Nonnull final String name, final int parallelism, final boolean asyncMode) {
        super(parallelism, new ConfiguredForkJoinWorkerThreadFactory(name), Thread.getDefaultUncaughtExceptionHandler(),
                false);
        configure();
    }

    private void configure() {
        /*
         * All executors should be shutdown on application shutdown.
         */
        ShutdownHookManager.register(shutdownHook);
    }

    private void unconfigure() {
        if (!isShutdown()) {
            ShutdownHookManager.unregister(shutdownHook);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        unconfigure();
    }

    @Override
    public List<Runnable> shutdownNow() {
        final List<Runnable> l = super.shutdownNow();
        unconfigure();
        return l;
    }

    public boolean awaitTermination() throws InterruptedException {
        return awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public boolean awaitQuiescence() {
        return awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public void execute(final ForkJoinTask<?> task) {
        //cannot wrap this to update parent thread name
        super.execute(task);
    }

    @Override
    public void execute(final Runnable task) {
        try {
            super.execute(WrappedRunnable.newInstance(null, task));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ForkJoinTask<T> submit(final Callable<T> task) {
        try {
            return super.submit(WrappedCallable.newInstance(null, task));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ForkJoinTask<T> submit(final ForkJoinTask<T> task) {
        //cannot wrap this to update parent thread name
        return super.submit(task);
    }

    @Override
    public ForkJoinTask<?> submit(final Runnable task) {
        try {
            return super.submit(WrappedRunnable.newInstance(null, task));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ForkJoinTask<T> submit(final Runnable task, final T result) {
        try {
            return super.submit(WrappedRunnable.newInstance(null, task), result);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T invoke(final ForkJoinTask<T> task) {
        //cannot wrap this to update parent thread name
        return super.invoke(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) {
        try {
            return super.invokeAll(WrappedCallable.newInstance(null, tasks));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return super.invokeAll(WrappedCallable.newInstance(null, tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return super.invokeAny(WrappedCallable.newInstance(null, tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(WrappedCallable.newInstance(null, tasks), timeout, unit);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        try {
            return super.newTaskFor(WrappedCallable.newInstance(null, callable));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        try {
            return super.newTaskFor(WrappedRunnable.newInstance(null, runnable), value);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
