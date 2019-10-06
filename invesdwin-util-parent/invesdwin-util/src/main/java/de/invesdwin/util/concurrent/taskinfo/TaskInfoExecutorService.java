package de.invesdwin.util.concurrent.taskinfo;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoCallable;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoRunnable;

@ThreadSafe
public class TaskInfoExecutorService implements ExecutorService {

    private final String taskName;
    private final ExecutorService delegate;

    public TaskInfoExecutorService(final String taskName, final ExecutorService delegate) {
        this.taskName = taskName;
        this.delegate = delegate;
    }

    @Override
    public void execute(final Runnable command) {
        delegate.execute(TaskInfoRunnable.of(taskName, command));
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return delegate.submit(TaskInfoCallable.of(taskName, task));
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return delegate.submit(TaskInfoRunnable.of(taskName, task), result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return delegate.submit(TaskInfoRunnable.of(taskName, task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(TaskInfoCallable.of(taskName, tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(TaskInfoCallable.of(taskName, tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return delegate.invokeAny(TaskInfoCallable.of(taskName, tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(TaskInfoCallable.of(taskName, tasks), timeout, unit);
    }

}
