package de.invesdwin.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.IFastIterableMap;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.concurrent.future.InterruptingFuture;
import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;
import de.invesdwin.util.concurrent.internal.IWrappedExecutorServiceInternal;
import de.invesdwin.util.concurrent.internal.WrappedCallable;
import de.invesdwin.util.concurrent.internal.WrappedRunnable;
import de.invesdwin.util.concurrent.internal.WrappedThreadFactory;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class WrappedExecutorService implements ListeningExecutorService {

    private static final Duration FIXED_THREAD_KEEPALIVE_TIMEOUT = new Duration(60, FTimeUnit.SECONDS);

    protected final IWrappedExecutorServiceInternal internal = new IWrappedExecutorServiceInternal() {

        @Override
        public IExecutorExceptionHandler getExecutorExceptionHandler() {
            return WrappedExecutorService.this.getExecutorExceptionHandler();
        }

        @Override
        public boolean isKeepThreadLocals() {
            return WrappedExecutorService.this.isKeepThreadLocals();
        }

        @Override
        public AtomicBoolean getDynamicThreadName() {
            return dynamicThreadName;
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
    private final WrappedExecutorServiceFinalizer finalizer;
    private final Lock pendingCountLock;
    private final IFastIterableMap<Integer, PendingCountCondition> pendingCount_condition = ILockCollectionFactory
            .getInstance(true)
            .newFastIterableMap();
    private final IFastIterableSet<IPendingCountListener> pendingCountListeners = ILockCollectionFactory
            .getInstance(true)
            .newFastIterableLinkedSet();
    private final AtomicInteger pendingCount = new AtomicInteger();
    private final Object pendingCountWaitLock = new Object();
    private final AtomicBoolean dynamicThreadName = new AtomicBoolean(true);
    private volatile boolean keepThreadLocals = true;
    private final String name;
    private final PendingCountCondition zeroPendingCountCondition;
    private final PendingCountCondition fullPendingCountCondition;
    private volatile PendingCountCondition waitOnFullPendingCountCondition;
    private volatile IExecutorExceptionHandler executorExceptionHandler = Throwables
            .getDefaultExecutorExceptionHandler();

    public WrappedExecutorService(final ExecutorService delegate, final String name) {
        //also check startsWith for nested executor
        if (Strings.isBlankOrNullText(name) || Strings.startsWithIgnoreCase(name, Strings.NULL_TEXT)) {
            throw new NullPointerException("name should not be blank or start with null: " + name);
        }
        this.name = name;
        this.finalizer = new WrappedExecutorServiceFinalizer(delegate, configure(delegate), newShutdownHook(delegate));
        this.pendingCountLock = Locks
                .newReentrantLock(WrappedExecutorService.class.getSimpleName() + "_" + name + "_pendingCountLock");
        this.zeroPendingCountCondition = getOrCreatePendingCountCondition(0);
        this.fullPendingCountCondition = getOrCreatePendingCountCondition(getMaximumPoolSize());
        this.waitOnFullPendingCountCondition = zeroPendingCountCondition;
        finalizer.register(this);
    }

    public void setFinalizerEnabled(final boolean finalizerEnabled) {
        if (!finalizerEnabled) {
            finalizer.unregister();
        } else {
            finalizer.register(this);
        }
    }

    public boolean isExecutorThread() {
        return name.equals(Threads.getCurrentThreadPoolName());
    }

    protected IShutdownHook newShutdownHook(final ExecutorService delegate) {
        return staticNewShutdownHook(delegate);
    }

    /**
     * Prevent reference leak to this instance by using a static method
     */
    protected static IShutdownHook staticNewShutdownHook(final ExecutorService delegate) {
        return () -> delegate.shutdownNow();
    }

    public WrappedExecutorService setExecutorExceptionHandler(
            final IExecutorExceptionHandler executorExceptionHandler) {
        if (executorExceptionHandler == null) {
            this.executorExceptionHandler = Throwables.getDefaultExecutorExceptionHandler();
        } else {
            this.executorExceptionHandler = executorExceptionHandler;
        }
        return this;
    }

    public IExecutorExceptionHandler getExecutorExceptionHandler() {
        return executorExceptionHandler;
    }

    public WrappedExecutorService setKeepThreadLocals(final boolean keepThreadLocals) {
        this.keepThreadLocals = keepThreadLocals;
        return this;
    }

    public boolean isKeepThreadLocals() {
        return keepThreadLocals;
    }

    public WrappedExecutorService setDynamicThreadName(final boolean dynamicThreadName) {
        this.dynamicThreadName.set(dynamicThreadName);
        return this;
    }

    public boolean isDynamicThreadName() {
        return dynamicThreadName.get();
    }

    public String getName() {
        return name;
    }

    private void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (isWaitOnFullPendingCount() && !skipWaitOnFullPendingCount) {
            if (pendingCount.get() >= fullPendingCountCondition.getLimit()) {
                /*
                 * Only one waiting thread may be woken up when this limit is reached, this is ensured by the while loop
                 * inside awaitPendingCount and the outer lock
                 */
                synchronized (pendingCountWaitLock) {
                    /*
                     * putting another while loop here significantly reduces performance since it seems to spin a lot.
                     * Though that loop is not needed here anyway
                     */
                    awaitPendingCount(fullPendingCountCondition);
                    notifyPendingCountListeners(pendingCount.incrementAndGet());
                }
            } else {
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
        if (!pendingCount_condition.isEmpty()) {
            final PendingCountCondition[] conditionArray = pendingCount_condition
                    .asValueArray(PendingCountCondition.EMPTY_ARRAY);
            for (int i = 0; i < conditionArray.length; i++) {
                final PendingCountCondition condition = conditionArray[i];
                if (currentPendingCount <= condition.getLimit()) {
                    pendingCountLock.lock();
                    try {
                        condition.getCondition().signalAll();
                    } finally {
                        pendingCountLock.unlock();
                    }
                }
            }
        }
        if (!pendingCountListeners.isEmpty()) {
            final IPendingCountListener[] listenerArray = pendingCountListeners
                    .asArray(IPendingCountListener.EMPTY_ARRAY);
            for (int i = 0; i < listenerArray.length; i++) {
                listenerArray[i].onPendingCountChanged(currentPendingCount);
            }
        }
    }

    protected ListeningExecutorService configure(final ExecutorService delegate) {
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

    public boolean isWaitOnFullPendingCount() {
        return waitOnFullPendingCountCondition.getLimit() > 0;
    }

    public int getWaitOnFullPendingCount() {
        return waitOnFullPendingCountCondition.getLimit();
    }

    public WrappedExecutorService setWaitOnFullPendingCount(final boolean waitOnFullPendingCount) {
        if (waitOnFullPendingCount) {
            this.waitOnFullPendingCountCondition = fullPendingCountCondition;
        } else {
            this.waitOnFullPendingCountCondition = zeroPendingCountCondition;
        }
        return this;
    }

    public WrappedExecutorService setWaitOnFullPendingCount(final int waitOnFullPendingCount) {
        if (waitOnFullPendingCount <= 0) {
            this.waitOnFullPendingCountCondition = zeroPendingCountCondition;
        } else {
            this.waitOnFullPendingCountCondition = getOrCreatePendingCountCondition(waitOnFullPendingCount);
        }
        return this;
    }

    @Override
    public void shutdown() {
        finalizer.delegate.shutdown();
        finalizer.close();
    }

    @Override
    public List<Runnable> shutdownNow() {
        final List<Runnable> l = finalizer.delegate.shutdownNow();
        finalizer.close();
        return l;
    }

    @Override
    public boolean isShutdown() {
        return finalizer.delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return finalizer.delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return finalizer.delegate.awaitTermination(timeout, unit);
    }

    public boolean awaitTermination() throws InterruptedException {
        return awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public boolean awaitTerminationNoInterrupt() {
        try {
            return awaitTermination();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    public void awaitPendingCount(final PendingCountCondition condition) throws InterruptedException {
        if (getPendingCount() > condition.getLimit()) {
            pendingCountLock.lock();
            try {
                while (getPendingCount() > condition.getLimit()) {
                    condition.getCondition().await();
                }
            } finally {
                pendingCountLock.unlock();
            }
        }
    }

    public PendingCountCondition getOrCreatePendingCountCondition(final int limit) {
        PendingCountCondition condition = pendingCount_condition.get(limit);
        if (condition == null) {
            synchronized (pendingCount_condition) {
                condition = pendingCount_condition.get(limit);
                if (condition == null) {
                    condition = new PendingCountCondition(limit, pendingCountLock.newCondition());
                    pendingCount_condition.put(limit, condition);
                }
            }
        }
        return condition;
    }

    protected void throwIfInterrupted() throws InterruptedException {
        Threads.throwIfInterrupted();
    }

    public void awaitPendingCountFull() throws InterruptedException {
        awaitPendingCount(fullPendingCountCondition);
    }

    public void awaitPendingCountZero() throws InterruptedException {
        awaitPendingCount(zeroPendingCountCondition);
    }

    public int getMaximumPoolSize() {
        final ExecutorService delegate = finalizer.originalDelegate;
        if (delegate instanceof java.util.concurrent.ThreadPoolExecutor) {
            final java.util.concurrent.ThreadPoolExecutor cDelegate = (java.util.concurrent.ThreadPoolExecutor) delegate;
            return cDelegate.getMaximumPoolSize();
        }
        return 0;
    }

    public PendingCountCondition getFullPendingCountCondition() {
        return fullPendingCountCondition;
    }

    @Override
    public void execute(final Runnable command) {
        try {
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, true, command);
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

    protected ListeningExecutorService getWrappedInstance() {
        return finalizer.delegate;
    }

    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        try {
            final WrappedCallable<T> callable = WrappedCallable.newInstance(internal, false, task);
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
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, false, task);
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
            final WrappedRunnable runnable = WrappedRunnable.newInstance(internal, false, task);
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
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, false, tasks);
        final List<Future<T>> futures = getWrappedInstance().invokeAll(callables);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, false, tasks);
        final List<Future<T>> futures = getWrappedInstance().invokeAll(callables, timeout, unit);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, false, tasks);
        final T result = getWrappedInstance().invokeAny(callables);
        maybeCancelled(callables);
        return result;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        final List<WrappedCallable<T>> callables = WrappedCallable.newInstance(internal, false, tasks);
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

    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        }
        final ListeningExecutorService delegateCopy = finalizer.delegate;
        if (delegateCopy != null) {
            if (delegateCopy instanceof WrappedExecutorService) {
                final WrappedExecutorService cDelegateCopy = (WrappedExecutorService) delegateCopy;
                return cDelegateCopy.unwrap(type);
            } else if (type.isAssignableFrom(delegateCopy.getClass())) {
                return (T) delegateCopy;
            }
        }
        return null;
    }

    public static final class PendingCountCondition {

        public static final PendingCountCondition[] EMPTY_ARRAY = new PendingCountCondition[0];

        private final int limit;
        private final Condition condition;

        private PendingCountCondition(final int limit, final Condition condition) {
            this.limit = limit;
            this.condition = condition;
        }

        public int getLimit() {
            return limit;
        }

        public Condition getCondition() {
            return condition;
        }

    }

    private static final class WrappedExecutorServiceFinalizer extends AFinalizer {

        private ExecutorService originalDelegate;
        private ListeningExecutorService delegate;
        @GuardedBy("this")
        private IShutdownHook shutdownHook;

        private WrappedExecutorServiceFinalizer(final ExecutorService originalDelegate,
                final ListeningExecutorService delegate, final IShutdownHook shutdownHook) {
            this.originalDelegate = originalDelegate;
            this.delegate = delegate;
            this.shutdownHook = shutdownHook;
            /*
             * All executors should be shutdown on application shutdown.
             */
            if (shutdownHook != null) {
                ShutdownHookManager.register(shutdownHook);
            }
        }

        @Override
        protected void clean() {
            if (originalDelegate != null) {
                originalDelegate.shutdownNow();
                originalDelegate = null;
                delegate = null;
            }
            if (shutdownHook != null) {
                ShutdownHookManager.unregister(shutdownHook);
            }
            shutdownHook = null;
        }

        @Override
        protected boolean isCleaned() {
            return originalDelegate.isShutdown();
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

}
