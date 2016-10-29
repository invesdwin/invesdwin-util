package de.invesdwin.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

/**
 * As an alternative to the java executors class. Here more conventions are kept for all executors.
 * 
 * @author subes
 * 
 */
@Immutable
public final class Executors {

    private static int cpuThreadPoolCount = Runtime.getRuntime().availableProcessors();

    private Executors() {}

    /**
     * @see java.util.concurrent.Executors.newCachedThreadPool
     */
    public static WrappedExecutorService newCachedThreadPool(final String name) {
        final java.util.concurrent.ThreadPoolExecutor ex = (java.util.concurrent.ThreadPoolExecutor) java.util.concurrent.Executors
                .newCachedThreadPool();
        return new WrappedExecutorService(ex, name);
    }

    /**
     * @see java.util.concurrent.Executors.newFixedThreadPool
     */
    public static WrappedExecutorService newFixedThreadPool(final String name, final int nThreads) {
        final java.util.concurrent.ThreadPoolExecutor ex = (java.util.concurrent.ThreadPoolExecutor) java.util.concurrent.Executors
                .newFixedThreadPool(nThreads);
        return new WrappedExecutorService(ex, name);
    }

    /**
     * @see java.util.concurrent.Executors.newScheduledThreadPool
     */
    public static WrappedScheduledExecutorService newScheduledThreadPool(final String name) {
        final java.util.concurrent.ScheduledThreadPoolExecutor ex = (java.util.concurrent.ScheduledThreadPoolExecutor) java.util.concurrent.Executors
                .newScheduledThreadPool(Integer.MAX_VALUE);
        return new WrappedScheduledExecutorService(ex, name);
    }

    /**
     * @see java.util.concurrent.Executors.newScheduledThreadPool
     */
    public static WrappedScheduledExecutorService newScheduledThreadPool(final String name, final int corePoolSize) {
        final java.util.concurrent.ScheduledThreadPoolExecutor ex = (java.util.concurrent.ScheduledThreadPoolExecutor) java.util.concurrent.Executors
                .newScheduledThreadPool(corePoolSize);
        return new WrappedScheduledExecutorService(ex, name);
    }

    public static WrappedExecutorService newFixedCallerRunsThreadPool(final String name, final int nThreads) {
        final java.util.concurrent.ThreadPoolExecutor ex = new java.util.concurrent.ThreadPoolExecutor(nThreads,
                nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(nThreads),
                new CallerRunsPolicy());
        return new WrappedExecutorService(ex, name);
    }

    /**
     * Returns the number of cpu cores for ThreadPools that are cpu intensive.
     */
    public static int getCpuThreadPoolCount() {
        return Executors.cpuThreadPoolCount;
    }

    public static void setCpuThreadPoolCount(final int cpuThreadPoolCount) {
        Executors.cpuThreadPoolCount = cpuThreadPoolCount;
    }

    public static ConfiguredForkJoinPool newForkJoinPool(final String name, final int parallelism) {
        return new ConfiguredForkJoinPool(name, parallelism, false);
    }

    public static ConfiguredForkJoinPool newAsyncForkJoinPool(final String name, final int parallelism) {
        return new ConfiguredForkJoinPool(name, parallelism, true);
    }

}
