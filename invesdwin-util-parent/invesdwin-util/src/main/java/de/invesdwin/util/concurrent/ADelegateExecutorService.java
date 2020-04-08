package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ADelegateExecutorService implements ExecutorService {

    private final ExecutorService delegate;

    public ADelegateExecutorService(final ExecutorService delegate) {
        this.delegate = delegate;
    }

    protected abstract Runnable newRunnable(Runnable runnable);

    protected abstract <T> Callable<T> newCallable(Callable<T> callable);

    public ExecutorService getDelegate() {
        return delegate;
    }

    @Override
    public void execute(final Runnable command) {
        getDelegate().execute(newRunnable(command));
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
    public <T> Future<T> submit(final Callable<T> task) {
        return getDelegate().submit(newCallable(task));
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return getDelegate().submit(newRunnable(task), result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return getDelegate().submit(newRunnable(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getDelegate().invokeAll(newCallables(tasks));
    }

    protected <T> Collection<? extends Callable<T>> newCallables(final Collection<? extends Callable<T>> tasks) {
        final List<Callable<T>> wrapped = new ArrayList<>(tasks.size());
        for (final Callable<T> task : tasks) {
            wrapped.add(newCallable(task));
        }
        return wrapped;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return getDelegate().invokeAll(newCallables(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return getDelegate().invokeAny(newCallables(tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getDelegate().invokeAny(newCallables(tasks), timeout, unit);
    }

}
