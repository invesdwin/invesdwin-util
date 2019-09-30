package de.invesdwin.util.concurrent.priority;

import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

/**
 * https://stackoverflow.com/questions/3198660/java-executors-how-can-i-set-task-priority
 * 
 * @author subes
 *
 */
@ThreadSafe
public class PriorityThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    public PriorityThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
            final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new PriorityBlockingQueue<Runnable>(10, PriorityFuture.COMPARATOR), threadFactory);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        final RunnableFuture<T> newTaskFor = super.newTaskFor(runnable, value);
        final double priority;
        if (runnable instanceof IPriorityProvider) {
            final IPriorityProvider provider = (IPriorityProvider) runnable;
            priority = provider.getPriority();
        } else {
            priority = IPriorityProvider.MISSING_PRIORITY;
        }
        return new PriorityFuture<T>(newTaskFor, priority);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        final RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        final double priority;
        if (callable instanceof IPriorityProvider) {
            final IPriorityProvider provider = (IPriorityProvider) callable;
            priority = provider.getPriority();
        } else {
            priority = IPriorityProvider.MISSING_PRIORITY;
        }
        return new PriorityFuture<T>(newTaskFor, priority);
    }

}
