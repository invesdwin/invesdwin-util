package de.invesdwin.util.concurrent.priority;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.UnsafeField;

/**
 * https://stackoverflow.com/questions/3198660/java-executors-how-can-i-set-task-priority
 * 
 * @author subes
 *
 */
@ThreadSafe
public class PriorityThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    @SuppressWarnings({ "restriction" })
    private static final sun.misc.Unsafe UNSAFE;
    private static final Class<?> TRUSTED_LISTENABLE_FUTURE_TASK_CLASS;
    private static final MethodHandle TRUSTED_LISTENABLE_FUTURE_TASK_GETTER;
    private static final Class<?> TRUSTED_FUTURE_INTERRUPTIBLE_TASK_CLASS;
    private static final UnsafeField<?> TRUSTED_FUTURE_INTERRUPTIBLE_TASK_FIELD;
    private static final Class<?> TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_CLASS;
    private static final UnsafeField<?> TRUSTED_FUTURE_INTERRUPTIBLE_ASYNC_TASK_FIELD;
    private static final Class<Object> RUNNABLE_ADAPTER_CLASS;
    private static final UnsafeField<?> RUNNABLE_ADAPTER_FIELD;

    static {
        try {
            UNSAFE = Reflections.getUnsafe();
            final Lookup lookup = MethodHandles.lookup();
            TRUSTED_LISTENABLE_FUTURE_TASK_CLASS = Reflections
                    .classForName("com.google.common.util.concurrent.TrustedListenableFutureTask");
            final Field trustedListenableFutureTaskField = Reflections.findField(TRUSTED_LISTENABLE_FUTURE_TASK_CLASS,
                    "task");
            Reflections.makeAccessible(trustedListenableFutureTaskField);
            TRUSTED_LISTENABLE_FUTURE_TASK_GETTER = lookup.unreflectGetter(trustedListenableFutureTaskField);

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
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

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
        final double priority = extractPriority(runnable);
        return new PriorityFuture<T>(newTaskFor, priority);
    }

    public double extractPriority(final Runnable runnable) {
        try {
            Object unwrapped = runnable;
            while (unwrapped != null) {
                if (unwrapped instanceof IPriorityProvider) {
                    final IPriorityProvider provider = (IPriorityProvider) unwrapped;
                    return provider.getPriority();
                } else if (TRUSTED_LISTENABLE_FUTURE_TASK_CLASS.isAssignableFrom(unwrapped.getClass())) {
                    unwrapped = TRUSTED_LISTENABLE_FUTURE_TASK_GETTER.invoke(unwrapped);
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

    @Override
    public void execute(final Runnable command) {
        super.execute(newTaskFor(command, null));
    }

}
