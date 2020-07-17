package de.invesdwin.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.concurrent.future.InterruptingFuture;
import de.invesdwin.util.concurrent.internal.IWrappedExecutorServiceInternal;
import de.invesdwin.util.concurrent.internal.WrappedCallable;
import de.invesdwin.util.concurrent.internal.WrappedRunnable;
import de.invesdwin.util.concurrent.internal.WrappedThreadFactory;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public class WrappedExecutorService implements ListeningExecutorService {

    private static final Duration FIXED_THREAD_KEEPALIVE_TIMEOUT = new Duration(60, FTimeUnit.SECONDS);

    protected final IWrappedExecutorServiceInternal internal = new IWrappedExecutorServiceInternal() {

        @Override
        public boolean isLogExceptions() {
            return WrappedExecutorService.this.isLogExceptions();
        }

        @Override
        public boolean isDynamicThreadName() {
            return WrappedExecutorService.this.isDynamicThreadName();
        }

        @Override
        public void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
            WrappedExecutorService.this.incrementPendingCount(skipWaitOnFullPendingCount);
        }

        @Override
        public void decrementPendingCount() {
            WrappedExecutorService.this.decrementPendingCount();
        }

        @Override
        public String getName() {
            return WrappedExecutorService.this.getName();
        }

    };
    private final Lock pendingCountLock;
    private final ALoadingCache<Integer, Condition> pendingCount_condition = new ALoadingCache<Integer, Condition>() {
        @Override
        protected Condition loadValue(final Integer key) {
            return pendingCountLock.newCondition();
        }
    };
    private final IFastIterableSet<IPendingCountListener> pendingCountListeners = ILockCollectionFactory
            .getInstance(true)
            .newFastIterableLinkedSet();
    private final AtomicInteger pendingCount = new AtomicInteger();
    private final Object pendingCountWaitLock = new Object();
    private final ListeningExecutorService delegate;
    private volatile boolean logExceptions = true;
    private volatile boolean waitOnFullPendingCount = false;
    private volatile boolean dynamicThreadName = true;
    private final String name;

    @GuardedBy("this")
    private IShutdownHook shutdownHook;

    protected WrappedExecutorService(final ExecutorService delegate, final String name) {
        this.shutdownHook = newShutdownHook(delegate);
        this.name = name;
        this.pendingCountLock = Locks
                .newReentrantLock(WrappedExecutorService.class.getSimpleName() + "_" + name + "_pendingCountLock");
        this.delegate = configure(delegate);
    }

    protected IShutdownHook newShutdownHook(final ExecutorService delegate) {
        return staticNewShutdownHook(delegate);
    }

    /**
     * Prevent reference leak to this instance by using a static method
     */
    protected static IShutdownHook staticNewShutdownHook(final ExecutorService delegate) {
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

    public String getName() {
        return name;
    }

    private void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (waitOnFullPendingCount && !skipWaitOnFullPendingCount) {
            synchronized (pendingCountWaitLock) {
                //Only one waiting thread may be woken up when this limit is reached!
                while (pendingCount.get() >= getFullPendingCount()) {
                    awaitPendingCount(getMaximumPoolSize() - 1);
                }
                notifyPendingCountListeners(pendingCount.incrementAndGet());
            }
        } else {
            notifyPendingCountListeners(pendingCount.incrementAndGet());
        }
    }

    private void decrementPendingCount() {
        notifyPendingCountListeners(pendingCount.decrementAndGet());
    }

    private void notifyPendingCountListeners(final int currentPendingCount) {
        pendingCountLock.lock();
        try {
            if (!pendingCount_condition.isEmpty()) {
                for (final Entry<Integer, Condition> e : pendingCount_condition.entrySet()) {
                    final Integer limit = e.getKey();
                    if (currentPendingCount <= limit) {
                        final Condition condition = e.getValue();
                        if (condition != null) {
                            condition.signalAll();
                        }
                    }
                }
            }
            if (!pendingCountListeners.isEmpty()) {
                final IPendingCountListener[] array = pendingCountListeners.asArray(IPendingCountListener.class);
                for (int i = 0; i < array.length; i++) {
                    array[i].onPendingCountChanged(currentPendingCount);
                }
            }
        } finally {
            pendingCountLock.unlock();
        }
    }

    protected ListeningExecutorService configure(final ExecutorService delegate) {
        /*
         * All executors should be shutdown on application shutdown.
         */
        if (shutdownHook != null) {
            ShutdownHookManager.register(shutdownHook);
        }

        if (delegate instanceof java.util.concurrent.ThreadPoolExecutor) {
            final java.util.concurrent.ThreadPoolExecutor cDelegate = (java.util.concurrent.ThreadPoolExecutor) delegate;
            /*
             * All threads should stop after 60 seconds of idle time
             */
            cDelegate.setKeepAliveTime(FIXED_THREAD_KEEPALIVE_TIMEOUT.longValue(),
                    FIXED_THREAD_KEEPALIVE_TIMEOUT.getTimeUnit().timeUnitValue());
            /*
             * Fixes non starting with corepoolsize of 0 and not filled queue (Java Concurrency In Practice Chapter
             * 8.3.1). If this bug can occur, an exception would be thrown here.
             */
            cDelegate.allowCoreThreadTimeOut(true);
            /*
             * Named threads improve debugging.
             */
            final WrappedThreadFactory threadFactory;
            if (cDelegate.getThreadFactory() instanceof WrappedThreadFactory) {
                threadFactory = (WrappedThreadFactory) cDelegate.getThreadFactory();
            } else {
                threadFactory = new WrappedThreadFactory(name, cDelegate.getThreadFactory());
                cDelegate.setThreadFactory(threadFactory);
            }
            threadFactory.setParent(internal);
        }

        return decorate(delegate);
    }

    protected ListeningExecutorService decorate(final ExecutorService delegate) {
        return MoreExecutors.listeningDecorator(delegate);
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

    public ListeningExecutorService getWrappedInstance() {
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

    public int getPendingCount() {
        return pendingCount.get();
    }

    /**
     * Waits for pendingCount to shrink to a given limit size.
     * 
     * WARNING: Pay attention when using this feature so that executors don't have circular task dependencies. If they
     * depend on each others pendingCount, this may cause a deadlock!
     */
    public void awaitPendingCount(final int limit) throws InterruptedException {
        pendingCountLock.lock();
        try {
            final Condition condition = pendingCount_condition.get(limit);
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

    public int getMaximumPoolSize() {
        final ExecutorService delegate = getWrappedInstance();
        if (delegate instanceof java.util.concurrent.ThreadPoolExecutor) {
            final java.util.concurrent.ThreadPoolExecutor cDelegate = (java.util.concurrent.ThreadPoolExecutor) delegate;
            return cDelegate.getMaximumPoolSize();
        }
        return 0;
    }

    public int getFullPendingCount() {
        return getMaximumPoolSize();
    }

    @Override
    public void execute(final Runnable command) {
        try {
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, command);
            try {
                getWrappedInstance().execute(runnable);
            } catch (final RejectedExecutionException e) {
                maybeCancelled(runnable);
                throw e;
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        try {
            final WrappedCallable<T> callable = WrappedCallable.newInstance(internal, task);
            final ListenableFuture<T> futures = getWrappedInstance().submit(callable);
            maybeCancelledInFuture(callable, futures);
            return futures;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<T>();
        }
    }

    @Override
    public <T> ListenableFuture<T> submit(final Runnable task, final T result) {
        try {
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, task);
            final ListenableFuture<T> futures = getWrappedInstance().submit(runnable, result);
            maybeCancelledInFuture(runnable, futures);
            return futures;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<T>();
        }
    }

    @Override
    public ListenableFuture<?> submit(final Runnable task) {
        try {
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, task);
            final ListenableFuture<?> futures = getWrappedInstance().submit(runnable);
            maybeCancelledInFuture(runnable, futures);
            return futures;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, tasks);
        final List<Future<T>> futures = getWrappedInstance().invokeAll(callables);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, tasks);
        final List<Future<T>> futures = getWrappedInstance().invokeAll(callables, timeout, unit);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, tasks);
        final T result = getWrappedInstance().invokeAny(callables);
        maybeCancelled(callables);
        return result;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, tasks);
        final T result = getWrappedInstance().invokeAny(callables, timeout, unit);
        maybeCancelled(callables);
        return result;
    }

    public IFastIterableSet<IPendingCountListener> getPendingCountListeners() {
        return pendingCountListeners;
    }

    protected <T> void maybeCancelled(final List<WrappedCallable<T>> callables) {
        for (final WrappedCallable<T> callable : callables) {
            maybeCancelled(callable);
        }
    }

    protected <T> void maybeCancelled(final WrappedCallable<T> callable) {
        callable.maybeCancelled();
    }

    protected void maybeCancelled(final WrappedRunnable runnable) {
        runnable.maybeCancelled();
    }

    protected <T> void maybeCancelledInFuture(final List<WrappedCallable<T>> callables, final List<Future<T>> futures) {
        for (int i = 0; i < callables.size(); i++) {
            final WrappedCallable<T> callable = callables.get(i);
            final ListenableFuture<T> future = (ListenableFuture<T>) futures.get(i);
            maybeCancelledInFuture(callable, future);
        }
    }

    protected <T> void maybeCancelledInFuture(final WrappedCallable<T> callable, final ListenableFuture<T> future) {
        future.addListener(new Runnable() {
            @Override
            public void run() {
                if (future.isCancelled()) {
                    maybeCancelled(callable);
                }
            }
        }, Executors.SIMPLE_DISABLED_EXECUTOR);
    }

    protected void maybeCancelledInFuture(final WrappedRunnable runnable, final ListenableFuture<?> future) {
        future.addListener(new Runnable() {
            @Override
            public void run() {
                if (future.isCancelled()) {
                    maybeCancelled(runnable);
                }
            }
        }, Executors.SIMPLE_DISABLED_EXECUTOR);
    }

}
