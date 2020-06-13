package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@ThreadSafe
public abstract class ADelegateExecutorService implements ListeningExecutorService {

    private final ListeningExecutorService delegate;

    public ADelegateExecutorService(final ListeningExecutorService delegate) {
        this.delegate = delegate;
    }

    protected abstract Runnable newRunnable(Runnable runnable);

    protected abstract <T> Callable<T> newCallable(Callable<T> callable);

    public ListeningExecutorService getDelegate() {
        return delegate;
    }

    @Override
    public void execute(final Runnable command) {
        final Runnable runnable = newRunnable(command);
        try {
            getDelegate().execute(runnable);
        } catch (final RejectedExecutionException e) {
            maybeCancelled(runnable);
            throw e;
        }
    }

    @Override
    public void shutdown() {
        getDelegate().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return getDelegate().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return getDelegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return getDelegate().isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return getDelegate().awaitTermination(timeout, unit);
    }

    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        final Callable<T> callable = newCallable(task);
        final ListenableFuture<T> future = getDelegate().submit(callable);
        maybeCancelledInFuture(callable, future);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submit(final Runnable task, final T result) {
        final Runnable runnable = newRunnable(task);
        final ListenableFuture<T> future = getDelegate().submit(runnable, result);
        maybeCancelledInFuture(runnable, future);
        return future;
    }

    @Override
    public ListenableFuture<?> submit(final Runnable task) {
        final Runnable runnable = newRunnable(task);
        final ListenableFuture<?> future = getDelegate().submit(runnable);
        maybeCancelledInFuture(runnable, future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<? extends Callable<T>> callables = newCallables(tasks);
        final List<Future<T>> futures = getDelegate().invokeAll(callables);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    protected <T> List<? extends Callable<T>> newCallables(final Collection<? extends Callable<T>> tasks) {
        final List<Callable<T>> wrapped = new ArrayList<>(tasks.size());
        for (final Callable<T> task : tasks) {
            wrapped.add(newCallable(task));
        }
        return wrapped;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        final List<? extends Callable<T>> callables = newCallables(tasks);
        final List<Future<T>> futures = getDelegate().invokeAll(callables, timeout, unit);
        maybeCancelledInFuture(callables, futures);
        return futures;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        final List<? extends Callable<T>> callables = newCallables(tasks);
        final T result = getDelegate().invokeAny(callables);
        maybeCancelled(callables);
        return result;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        final List<? extends Callable<T>> callables = newCallables(tasks);
        final T result = getDelegate().invokeAny(callables, timeout, unit);
        maybeCancelled(callables);
        return result;
    }

    protected <T> void maybeCancelled(final List<? extends Callable<T>> callables) {
        for (final Callable<T> callable : callables) {
            maybeCancelled(callable);
        }
    }

    protected abstract <T> void maybeCancelled(Callable<T> callable);

    protected abstract void maybeCancelled(Runnable runnable);

    protected <T> void maybeCancelledInFuture(final List<? extends Callable<T>> callables,
            final List<Future<T>> futures) {
        for (int i = 0; i < callables.size(); i++) {
            final Callable<T> callable = callables.get(i);
            final ListenableFuture<T> future = (ListenableFuture<T>) futures.get(i);
            maybeCancelledInFuture(callable, future);
        }
    }

    protected <T> void maybeCancelledInFuture(final Callable<T> callable, final ListenableFuture<T> future) {
        future.addListener(new Runnable() {
            @Override
            public void run() {
                if (future.isCancelled()) {
                    maybeCancelled(callable);
                }
            }
        }, Executors.SIMPLE_DISABLED_EXECUTOR);
    }

    protected void maybeCancelledInFuture(final Runnable runnable, final ListenableFuture<?> future) {
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
