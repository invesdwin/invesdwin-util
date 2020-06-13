package de.invesdwin.util.concurrent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@ThreadSafe
public abstract class ASimpleDelegateExecutorService extends ADelegateExecutorService {

    public ASimpleDelegateExecutorService(final ListeningExecutorService delegate) {
        super(delegate);
    }

    @Override
    protected <T> void maybeCancelled(final List<? extends Callable<T>> callables) {
        //noop
    }

    @Override
    protected <T> void maybeCancelledInFuture(final List<? extends Callable<T>> callables,
            final List<Future<T>> futures) {
        //noop
    }

    @Override
    protected <T> void maybeCancelled(final Callable<T> callable) {
        //noop
    }

    @Override
    protected void maybeCancelled(final Runnable runnable) {
        //noop
    }

    @Override
    protected <T> void maybeCancelledInFuture(final Callable<T> callable, final ListenableFuture<T> future) {
        //noop
    }

    @Override
    protected void maybeCancelledInFuture(final Runnable runnable, final ListenableFuture<?> future) {
        //noop
    }

}
