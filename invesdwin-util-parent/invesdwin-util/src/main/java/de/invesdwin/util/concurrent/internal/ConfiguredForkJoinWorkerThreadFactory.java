package de.invesdwin.util.concurrent.internal;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;

@ThreadSafe
public class ConfiguredForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

    private final int threadpoolId = WrappedThreadFactory.THREADPOOL_IDS.incrementAndGet();
    private final AtomicInteger threadIds = new AtomicInteger();

    private IWrappedExecutorServiceInternal parent;
    private final String name;

    public ConfiguredForkJoinWorkerThreadFactory(final String name) {
        Assertions.checkNotNull(name);
        this.name = name;
    }

    public void setParent(final IWrappedExecutorServiceInternal parent) {
        Assertions.checkNull(this.parent);
        Assertions.checkEquals(name, parent.getName());
        this.parent = parent;
    }

    @Override
    public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
        final String parentThreadName = Thread.currentThread().getName();
        final ForkJoinWorkerThread t = new ForkJoinWorkerThread(pool) {
            /**
             * http://jsr166-concurrency.10961.n7.nabble.com/How-to-set-the-thread-group-of-the-ForkJoinPool-td1590.html
             */
            @Override
            protected void onStart() {
                super.onStart();
                final String curThreadName = threadpoolId + "-" + threadIds.incrementAndGet() + ":" + name;
                if (parent == null || parent.isDynamicThreadName()) {
                    setName(curThreadName + Threads.NESTED_THREAD_NAME_SEPARATOR + parentThreadName);
                } else {
                    setName(curThreadName);
                }
            }
        };
        /*
         * So that exceptions are still logged if runnables are sent into executors without futures being checked. This
         * keeps the default behaviour expected from normal threads.
         */
        if (t.getUncaughtExceptionHandler() != Thread.getDefaultUncaughtExceptionHandler()) {
            throw new IllegalArgumentException(
                    UncaughtExceptionHandler.class.getSimpleName() + " is not already set properly!");
        }
        return t;
    }

}
