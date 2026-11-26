package de.invesdwin.util.concurrent;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.future.Futures;

/**
 * Registers every task scheduled through it (execute/submit at any nesting depth) into a shared queue, so that the
 * caller can join the complete task tree - including nested fire-and-forget submissions - before continuing. Task
 * decoration is pass-through on purpose: runnables and callables run unmodified.
 */
@ThreadSafe
public class TrackingExecutorService extends ADelegateExecutorService {

    private final Queue<Future<?>> trackedTasks;

    public TrackingExecutorService(final ListeningExecutorService delegate,
            final Queue<Future<?>> trackedTasks) {
        super(delegate);
        Assertions.checkNotNull(trackedTasks);
        this.trackedTasks = trackedTasks;
    }

    /**
     * Waits for every future polled from the queue and keeps polling until it is empty; futures that a still running
     * task adds to the queue are picked up by the following polls, so this returns only when no queued submission is
     * pending anymore anywhere in the tree.
     */
    public static void waitUntilQuiescent(final Queue<Future<?>> trackedTasks) {
        Future<?> trackedTask;
        while ((trackedTask = trackedTasks.poll()) != null) {
            Futures.waitNoInterrupt(trackedTask);
        }
    }

    @Override
    protected Runnable newRunnable(final Runnable runnable) {
        return runnable;
    }

    @Override
    protected <T> Callable<T> newCallable(final Callable<T> callable) {
        return callable;
    }

    @Override
    protected void maybeCancelled(final Runnable runnable) {
        //no task decoration, nothing to cancel
    }

    @Override
    protected <T> void maybeCancelled(final Callable<T> callable) {
        //no task decoration, nothing to cancel
    }

    @Override
    public void execute(final Runnable command) {
        trackedTasks.add(super.submit(command));
    }

    @Override
    public ListenableFuture<?> submit(final Runnable task) {
        final ListenableFuture<?> future = super.submit(task);
        trackedTasks.add(future);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submit(final Callable<T> task) {
        final ListenableFuture<T> future = super.submit(task);
        trackedTasks.add(future);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submit(final Runnable task, final T result) {
        final ListenableFuture<T> future = super.submit(task, result);
        trackedTasks.add(future);
        return future;
    }

}
