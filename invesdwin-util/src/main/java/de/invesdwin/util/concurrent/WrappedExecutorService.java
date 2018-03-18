package de.invesdwin.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public class WrappedExecutorService implements ExecutorService {

    private static final Duration FIXED_THREAD_KEEPALIVE_TIMEOUT = new Duration(60, FTimeUnit.SECONDS);

    private final Lock pendingCountLock = new ReentrantLock();
    private final ALoadingCache<Long, Condition> pendingCount_limitListener = new ALoadingCache<Long, Condition>() {
        @Override
        protected Condition loadValue(final Long key) {
            return pendingCountLock.newCondition();
        }
    };
    private final AtomicLong pendingCount = new AtomicLong();
    private final Object pendingCountWaitLock = new Object();
    private final java.util.concurrent.ThreadPoolExecutor delegate;
    private volatile boolean logExceptions = false;
    private volatile boolean waitOnFullPendingCount = false;
    private volatile boolean dynamicThreadName;

    @GuardedBy("this")
    private IShutdownHook shutdownHook;

    protected WrappedExecutorService(final java.util.concurrent.ThreadPoolExecutor delegate, final String name) {
        this.delegate = delegate;
        this.shutdownHook = newShutdownHook(delegate);
        configure(name);
    }

    /**
     * Prevent reference leak to this instance by using a static method
     */
    private static IShutdownHook newShutdownHook(final java.util.concurrent.ThreadPoolExecutor delegate) {
        return new IShutdownHook() {
            @Override
            public void shutdown() throws Exception {
                delegate.shutdownNow();
            }
        };
    }

    public boolean isLogExceptions() {
        return logExceptions;
    }

    public WrappedExecutorService withLogExceptions(final boolean logExceptions) {
        this.logExceptions = logExceptions;
        return this;
    }

    public WrappedExecutorService withDynamicThreadName(final boolean dynamicThreadName) {
        this.dynamicThreadName = dynamicThreadName;
        return this;
    }

    public boolean isDynamicThreadName() {
        return dynamicThreadName;
    }

    void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (waitOnFullPendingCount && !skipWaitOnFullPendingCount) {
            synchronized (pendingCountWaitLock) {
                //Only one waiting thread may be woken up when this limit is reached!
                while (pendingCount.get() >= getFullPendingCount()) {
                    awaitPendingCount(getWrappedInstance().getMaximumPoolSize() - 1);
                }
                notifyPendingCountListeners(pendingCount.incrementAndGet());
            }
        } else {
            notifyPendingCountListeners(pendingCount.incrementAndGet());
        }
    }

    void decrementPendingCount() {
        notifyPendingCountListeners(pendingCount.decrementAndGet());
    }

    private void notifyPendingCountListeners(final Long currentPendingCount) {
        pendingCountLock.lock();
        try {
            for (final Long limit : pendingCount_limitListener.keySet()) {
                if (currentPendingCount <= limit) {
                    final Condition condition = pendingCount_limitListener.get(limit);
                    if (condition != null) {
                        condition.signalAll();
                    }
                }
            }
        } finally {
            pendingCountLock.unlock();
        }
    }

    private synchronized void configure(final String name) {
        /*
         * All executors should be shutdown on application shutdown.
         */
        ShutdownHookManager.register(shutdownHook);
        /*
         * All threads should stop after 60 seconds of idle time
         */
        delegate.setKeepAliveTime(FIXED_THREAD_KEEPALIVE_TIMEOUT.longValue(),
                FIXED_THREAD_KEEPALIVE_TIMEOUT.getTimeUnit().timeUnitValue());
        /*
         * Fixes non starting with corepoolsize von 0 and not filled queue (Java Conurrency In Practice Chapter 8.3.1).
         * If this bug can occur, a exception would be thrown here.
         */
        delegate.allowCoreThreadTimeOut(true);
        /*
         * Named threads improve debugging.
         */
        if (!(delegate.getThreadFactory() instanceof WrappedThreadFactory)) {
            delegate.setThreadFactory(new WrappedThreadFactory(name, delegate.getThreadFactory()));
        }
    }

    private synchronized void unconfigure() {
        if (shutdownHook != null) {
            ShutdownHookManager.unregister(shutdownHook);
        }
        shutdownHook = null;
    }

    public boolean isWaitOnFullPendingCount() {
        return waitOnFullPendingCount;
    }

    public WrappedExecutorService withWaitOnFullPendingCount(final boolean waitOnFullPendingCount) {
        this.waitOnFullPendingCount = waitOnFullPendingCount;
        return this;
    }

    public java.util.concurrent.ThreadPoolExecutor getWrappedInstance() {
        return delegate;
    }

    @Override
    public void shutdown() {
        getWrappedInstance().shutdown();
        unconfigure();
    }

    @Override
    public List<Runnable> shutdownNow() {
        final List<Runnable> l = getWrappedInstance().shutdownNow();
        unconfigure();
        return l;
    }

    @Override
    public boolean isShutdown() {
        return getWrappedInstance().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return getWrappedInstance().isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return getWrappedInstance().awaitTermination(timeout, unit);
    }

    public boolean awaitTermination() throws InterruptedException {
        return awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public long getPendingCount() {
        return pendingCount.get();
    }

    /**
     * Waits for pendingCount to shrink to a given limit size.
     * 
     * WARNING: Pay attention when using this feature so that executors don't have circular task dependencies. If they
     * depend on each others pendingCount, this may cause a deadlock!
     */
    public void awaitPendingCount(final long limit) throws InterruptedException {
        pendingCountLock.lock();
        try {
            final Condition condition = pendingCount_limitListener.get(limit);
            while (getPendingCount() > limit) {
                Threads.throwIfInterrupted();
                condition.await();
            }
        } finally {
            pendingCountLock.unlock();
        }
    }

    public void waitOnFullPendingCount() throws InterruptedException {
        awaitPendingCount(getFullPendingCount());
    }

    public int getFullPendingCount() {
        return getWrappedInstance().getMaximumPoolSize();
    }

    @Override
    public void execute(final Runnable command) {
        try {
            getWrappedInstance().execute(WrappedRunnable.newInstance(this, command));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        try {
            return getWrappedInstance().submit(WrappedCallable.newInstance(this, task));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<T>();
        }
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        try {
            return getWrappedInstance().submit(WrappedRunnable.newInstance(this, task), result);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<T>();
        }
    }

    @Override
    public Future<?> submit(final Runnable task) {
        try {
            return getWrappedInstance().submit(WrappedRunnable.newInstance(this, task));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getWrappedInstance().invokeAll(WrappedCallable.newInstance(this, tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return getWrappedInstance().invokeAll(WrappedCallable.newInstance(this, tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return getWrappedInstance().invokeAny(WrappedCallable.newInstance(this, tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getWrappedInstance().invokeAny(WrappedCallable.newInstance(this, tasks), timeout, unit);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        shutdown();
    }

}
