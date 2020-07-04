package de.invesdwin.util.concurrent.priority;

import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.reflection.UnsafeField;

/**
 * https://stackoverflow.com/questions/3198660/java-executors-how-can-i-set-task-priority
 * 
 * @author subes
 *
 */
@ThreadSafe
public class PriorityThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    private static final Class<?> TRUSTED_LISTENABLE_FUTURE_TASK_CLASS;
    private static final UnsafeField<?> TRUSTED_LISTENABLE_FUTURE_TASK_FIELD;
    private static final Class<?> TRUSTED_FUTURE_INTERRUPTIBLE_TASK_CLASS;
    private static final UnsafeField<?> TRUSTED_FUTURE_INTERRUPTIBLE_TASK_FIELD;
    private static final Class<?> TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_CLASS;
    private static final UnsafeField<?> TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_FIELD;
    private static final Class<Object> RUNNABLE_ADAPTER_CLASS;
    private static final UnsafeField<?> RUNNABLE_ADAPTER_FIELD;

    static {
        TRUSTED_LISTENABLE_FUTURE_TASK_CLASS = Reflections
                .classForName("com.google.common.util.concurrent.TrustedListenableFutureTask");
        TRUSTED_LISTENABLE_FUTURE_TASK_FIELD = new UnsafeField<Object>(
                Reflections.findField(TRUSTED_LISTENABLE_FUTURE_TASK_CLASS, "task"));

        TRUSTED_FUTURE_INTERRUPTIBLE_TASK_CLASS = Reflections.classForName(
                "com.google.common.util.concurrent.TrustedListenableFutureTask$TrustedFutureInterruptibleTask");
        TRUSTED_FUTURE_INTERRUPTIBLE_TASK_FIELD = new UnsafeField<Object>(
                Reflections.findField(TRUSTED_FUTURE_INTERRUPTIBLE_TASK_CLASS, "callable"));

        TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_CLASS = Reflections.classForName(
                "com.google.common.util.concurrent.TrustedListenableFutureTask$TrustedFutureInterruptibleAsyncTask");
        TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_FIELD = new UnsafeField<Object>(
                Reflections.findField(TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_CLASS, "callable"));

        RUNNABLE_ADAPTER_CLASS = Reflections.classForName("java.util.concurrent.Executors$RunnableAdapter");
        RUNNABLE_ADAPTER_FIELD = new UnsafeField<Object>(Reflections.findField(RUNNABLE_ADAPTER_CLASS, "task"));
    }

    public PriorityThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
            final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new PriorityBlockingQueue<Runnable>(10, PriorityFuture.COMPARATOR), threadFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        if (runnable instanceof PriorityFuture) {
            return (RunnableFuture<T>) runnable;
        }
        final RunnableFuture<T> newTaskFor = super.newTaskFor(runnable, value);
        final double priority = extractRunnablePriority(runnable);
        return new PriorityFuture<T>(newTaskFor, priority);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        final RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        final double priority = extractCallablePriority(callable);
        return new PriorityFuture<T>(newTaskFor, priority);
    }

    @Override
    public void execute(final Runnable command) {
        super.execute(newTaskFor(command, null));
    }

    private double extractRunnablePriority(final Runnable runnable) {
        try {
            Object unwrapped = runnable;
            while (unwrapped != null) {
                if (unwrapped instanceof IPriorityProvider) {
                    final IPriorityProvider provider = (IPriorityProvider) unwrapped;
                    return provider.getPriority();
                } else if (TRUSTED_LISTENABLE_FUTURE_TASK_CLASS.isAssignableFrom(unwrapped.getClass())) {
                    unwrapped = TRUSTED_LISTENABLE_FUTURE_TASK_FIELD.get(unwrapped);
                } else if (TRUSTED_FUTURE_INTERRUPTIBLE_TASK_CLASS.isAssignableFrom(unwrapped.getClass())) {
                    unwrapped = TRUSTED_FUTURE_INTERRUPTIBLE_TASK_FIELD.get(unwrapped);
                } else if (TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_CLASS.isAssignableFrom(unwrapped.getClass())) {
                    unwrapped = TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_FIELD.get(unwrapped);
                } else if (RUNNABLE_ADAPTER_CLASS.isAssignableFrom(unwrapped.getClass())) {
                    unwrapped = RUNNABLE_ADAPTER_FIELD.get(unwrapped);
                } else {
                    return IPriorityProvider.MISSING_PRIORITY;
                }
            }
            return IPriorityProvider.MISSING_PRIORITY;
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private double extractCallablePriority(final Callable<?> callable) {
        //there is no nesting by guava for callables since execute(command) is called
        if (callable instanceof IPriorityProvider) {
            final IPriorityProvider provider = (IPriorityProvider) callable;
            return provider.getPriority();
        } else {
            return IPriorityProvider.MISSING_PRIORITY;
        }
    }

}
