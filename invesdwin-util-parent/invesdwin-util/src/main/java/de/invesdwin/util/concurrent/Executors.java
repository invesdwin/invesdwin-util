package de.invesdwin.util.concurrent;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.concurrent.internal.WrappedThreadFactory;
import de.invesdwin.util.concurrent.priority.PriorityThreadPoolExecutor;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.shutdown.IShutdownHook;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * As an alternative to the java executors class. Here more conventions are kept for all executors.
 * 
 * @author subes
 * 
 */
@Immutable
public final class Executors {

    public static final ListeningExecutorService SIMPLE_DISABLED_EXECUTOR = MoreExecutors.newDirectExecutorService();

    private static final int MAX_CACHED_POOL_SIZE = 1000;
    private static int cpuThreadPoolCount = Runtime.getRuntime().availableProcessors();

    private Executors() {}

    /**
     * @see java.util.concurrent.Executors.newCachedThreadPool
     */
    public static WrappedExecutorService newCachedThreadPool(final String name) {
        final java.util.concurrent.ThreadPoolExecutor ex = (java.util.concurrent.ThreadPoolExecutor) java.util.concurrent.Executors
                .newCachedThreadPool(newFastThreadLocalThreadFactory(name));
        ex.setMaximumPoolSize(MAX_CACHED_POOL_SIZE);
        return new WrappedExecutorService(ex, name);
    }

    public static WrappedThreadFactory newFastThreadLocalThreadFactory(final String name) {
        return new WrappedThreadFactory(name, new DefaultThreadFactory(name));
    }

    /**
     * @see java.util.concurrent.Executors.newFixedThreadPool
     */
    public static WrappedExecutorService newFixedThreadPool(final String name, final int nThreads) {
        final int theads = Integers.max(1, nThreads);
        final java.util.concurrent.ThreadPoolExecutor ex = (java.util.concurrent.ThreadPoolExecutor) java.util.concurrent.Executors
                .newFixedThreadPool(theads, newFastThreadLocalThreadFactory(name));
        return new WrappedExecutorService(ex, name);
    }

    public static WrappedExecutorService newFixedPriorityThreadPool(final String name, final int nThreads) {
        final int theads = Integers.max(1, nThreads);
        final java.util.concurrent.ThreadPoolExecutor ex = new PriorityThreadPoolExecutor(theads, theads, 0L,
                TimeUnit.MILLISECONDS, newFastThreadLocalThreadFactory(name));
        return new WrappedExecutorService(ex, name);
    }

    /**
     * @see java.util.concurrent.Executors.newScheduledThreadPool
     */
    public static WrappedScheduledExecutorService newScheduledThreadPool(final String name) {
        final java.util.concurrent.ScheduledThreadPoolExecutor ex = (java.util.concurrent.ScheduledThreadPoolExecutor) java.util.concurrent.Executors
                .newScheduledThreadPool(MAX_CACHED_POOL_SIZE, newFastThreadLocalThreadFactory(name));
        return new WrappedScheduledExecutorService(ex, name).setDynamicThreadName(false);
    }

    /**
     * @see java.util.concurrent.Executors.newScheduledThreadPool
     */
    public static WrappedScheduledExecutorService newScheduledThreadPool(final String name, final int corePoolSize) {
        final int threads = Integers.max(1, corePoolSize);
        final java.util.concurrent.ScheduledThreadPoolExecutor ex = (java.util.concurrent.ScheduledThreadPoolExecutor) java.util.concurrent.Executors
                .newScheduledThreadPool(threads, newFastThreadLocalThreadFactory(name));
        return new WrappedScheduledExecutorService(ex, name).setDynamicThreadName(false);
    }

    public static WrappedExecutorService newFixedCallerRunsThreadPool(final String name, final int nThreads) {
        final int threads = Integers.max(1, nThreads);
        final java.util.concurrent.ThreadPoolExecutor ex = new java.util.concurrent.ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(threads),
                newFastThreadLocalThreadFactory(name), new CallerRunsPolicy());
        return new WrappedExecutorService(ex, name);
    }

    /**
     * Returns the number of cpu cores for ThreadPools that are cpu intensive.
     */
    public static int getCpuThreadPoolCount() {
        return Executors.cpuThreadPoolCount;
    }

    public static void setCpuThreadPoolCount(final int cpuThreadPoolCount) {
        Executors.cpuThreadPoolCount = Integers.max(1, cpuThreadPoolCount);
    }

    public static ConfiguredForkJoinPool newForkJoinPool(final String name, final int parallelism) {
        final int threads = Integers.max(1, parallelism);
        return new ConfiguredForkJoinPool(name, threads, false);
    }

    public static ConfiguredForkJoinPool newAsyncForkJoinPool(final String name, final int parallelism) {
        final int threads = Integers.max(1, parallelism);
        return new ConfiguredForkJoinPool(name, threads, true);
    }

    /**
     * This executor does not actually run tasks in parallel but instead runs them directly in the callers thread
     */
    public static WrappedExecutorService newDisabledExecutor(final String name) {
        return new WrappedExecutorService(MoreExecutors.newDirectExecutorService(), name) {

            {
                super.setDynamicThreadName(false);
                super.setFinalizerEnabled(false);
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
            public WrappedExecutorService setDynamicThreadName(final boolean dynamicThreadName) {
                //disabled
                return this;
            }
        };
    }

    public static boolean isDisabled(final Executor executor) {
        if (executor instanceof WrappedExecutorService) {
            final WrappedExecutorService cExecutor = (WrappedExecutorService) executor;
            return isDisabled(cExecutor);
        } else {
            return SIMPLE_DISABLED_EXECUTOR.getClass().isAssignableFrom(executor.getClass());
        }
    }

    public static boolean isDisabled(final WrappedExecutorService executor) {
        return executor.unwrap(SIMPLE_DISABLED_EXECUTOR.getClass()) != null;
    }

}
