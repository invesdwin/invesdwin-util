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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.internal.ConfiguredForkJoinWorkerThreadFactory;
import de.invesdwin.util.concurrent.internal.IWrappedExecutorServiceInternal;
import de.invesdwin.util.concurrent.internal.WrappedCallable;
import de.invesdwin.util.concurrent.internal.WrappedRunnable;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;

@ThreadSafe
public class ConfiguredForkJoinPool extends ForkJoinPool {

    protected final IWrappedExecutorServiceInternal internal = new IWrappedExecutorServiceInternal() {

        @Override
        public boolean isLogExceptions() {
            return ConfiguredForkJoinPool.this.isLogExceptions();
        }

        @Override
        public AtomicBoolean getDynamicThreadName() {
            return dynamicThreadName;
        }

        @Override
        public boolean isKeepThreadLocals() {
            return ConfiguredForkJoinPool.this.isKeepThreadLocals();
        }

        @Override
        public void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
            //noop
        }

        @Override
        public void decrementPendingCount() {
            //noop
        }

        @Override
        public String getName() {
            return ConfiguredForkJoinPool.this.getName();
        }

    };
    private volatile boolean logExceptions = true;
    private final AtomicBoolean dynamicThreadName = new AtomicBoolean(true);
    private volatile boolean keepThreadLocals = true;
    private final String name;

    private final IShutdownHook shutdownHook = new IShutdownHook() {
        @Override
        public void shutdown() throws Exception {
            shutdownNow();
        }
    };

    public ConfiguredForkJoinPool(final String name, final int parallelism, final boolean asyncMode) {
        super(parallelism, new ConfiguredForkJoinWorkerThreadFactory(name), Thread.getDefaultUncaughtExceptionHandler(),
                false);
        this.name = name;
        configure();
    }

    public ConfiguredForkJoinPool setLogExceptions(final boolean logExceptions) {
        this.logExceptions = logExceptions;
        return this;
    }

    public boolean isLogExceptions() {
        return logExceptions;
    }

    public ConfiguredForkJoinPool setKeepThreadLocals(final boolean keepThreadLocals) {
        this.keepThreadLocals = keepThreadLocals;
        return this;
    }

    public boolean isKeepThreadLocals() {
        return keepThreadLocals;
    }

    public ConfiguredForkJoinPool setDynamicThreadName(final boolean dynamicThreadName) {
        this.dynamicThreadName.set(dynamicThreadName);
        return this;
    }

    public boolean isDynamicThreadName() {
        return dynamicThreadName.get();
    }

    public String getName() {
        return name;
    }

    private void configure() {
        final ConfiguredForkJoinWorkerThreadFactory factory = (ConfiguredForkJoinWorkerThreadFactory) getFactory();
        factory.setParent(internal);
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
            super.execute(WrappedRunnable.newInstance(internal, task));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ForkJoinTask<T> submit(final Callable<T> task) {
        try {
            return super.submit(WrappedCallable.newInstance(internal, task));
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
            return super.submit(WrappedRunnable.newInstance(internal, task));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ForkJoinTask<T> submit(final Runnable task, final T result) {
        try {
            return super.submit(WrappedRunnable.newInstance(internal, task), result);
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
            return super.invokeAll(WrappedCallable.newInstance(internal, tasks));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return super.invokeAll(WrappedCallable.newInstance(internal, tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return super.invokeAny(WrappedCallable.newInstance(internal, tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(WrappedCallable.newInstance(internal, tasks), timeout, unit);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        try {
            return super.newTaskFor(WrappedCallable.newInstance(internal, callable));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        try {
            return super.newTaskFor(WrappedRunnable.newInstance(internal, runnable), value);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
