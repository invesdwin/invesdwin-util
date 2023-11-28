package de.invesdwin.util.concurrent.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.lang.string.Strings;

@ThreadSafe
public class WrappedThreadFactory implements ThreadFactory {

    public static final AtomicInteger THREADPOOL_IDS = new AtomicInteger();
    private final int threadpoolId = THREADPOOL_IDS.incrementAndGet();
    private final AtomicInteger threadIds = new AtomicInteger();

    private AtomicBoolean dynamicThreadName;
    private final String name;
    private final ThreadFactory delegate;

    public WrappedThreadFactory(final String name, final ThreadFactory delegate) {
        //also check startsWith for nested executor
        if (Strings.isBlankOrNullText(name) || Strings.startsWithAnyIgnoreCase(name, Strings.NULL_TEXT)) {
            throw new NullPointerException("name should not be blank or start with null: " + name);
        }
        Assertions.assertThat(delegate).isNotNull();
        this.name = name;
        this.delegate = delegate;
        Assertions.assertThat(delegate).isNotInstanceOf(WrappedThreadFactory.class);
    }

    public void setParent(final IWrappedExecutorServiceInternal parent) {
        Assertions.checkNull(this.dynamicThreadName);
        Assertions.checkEquals(name, parent.getName());
        this.dynamicThreadName = parent.getDynamicThreadName();
    }

    public String getName() {
        return name;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = delegate.newThread(() -> {
            Threads.setCurrentThreadPoolName(name);
            r.run();
        });

        final String curThreadName = threadpoolId + "-" + threadIds.incrementAndGet() + ":" + name;
        if (dynamicThreadName == null || dynamicThreadName.get()) {
            final String parentThreadName = Thread.currentThread().getName();
            t.setName(curThreadName + Threads.NESTED_THREAD_NAME_SEPARATOR + parentThreadName);
        } else {
            t.setName(curThreadName);
        }
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
