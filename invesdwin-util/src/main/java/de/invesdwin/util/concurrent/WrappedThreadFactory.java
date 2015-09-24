package de.invesdwin.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class WrappedThreadFactory implements ThreadFactory {

    public static final AtomicInteger THREADPOOL_IDS = new AtomicInteger();
    private final int threadpoolId = THREADPOOL_IDS.incrementAndGet();
    private final AtomicInteger threadIds = new AtomicInteger();

    private final String name;
    private final ThreadFactory delegate;

    public WrappedThreadFactory(@Nonnull final String name, @Nonnull final ThreadFactory delegate) {
        Assertions.assertThat(name).isNotNull();
        Assertions.assertThat(delegate).isNotNull();
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = delegate.newThread(r);

        final String parentThreadName = Thread.currentThread().getName();
        final String curThreadName = threadpoolId + "-" + threadIds.incrementAndGet() + ":" + name;
        t.setName(curThreadName + Threads.NESTED_THREAD_NAME_SEPARATOR + parentThreadName);
        /*
         * So that exceptions are still logged if runnables are sent into executors without futures being checked. This
         * keeps the default behaviour expected from normal threads.
         */
        t.setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
        return t;
    }

    public ThreadFactory getWrappedInstance() {
        return delegate;
    }
}
