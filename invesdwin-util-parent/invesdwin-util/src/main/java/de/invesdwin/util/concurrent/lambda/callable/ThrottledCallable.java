package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.future.ImmutableFuture;
import de.invesdwin.util.concurrent.future.ThrowableFuture;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public final class ThrottledCallable<T> implements Callable<T> {

    private final LoopInterruptedCheck throttleCheck;
    private final Callable<T> callable;
    private Future<T> throttledValue;

    private ThrottledCallable(final Duration throttleInterval, final Callable<T> callable) {
        this.throttleCheck = new LoopInterruptedCheck(throttleInterval) {
            @Override
            protected long getInitialNanoTime() {
                return Long.MIN_VALUE;
            }
        };
        this.callable = callable;
    }

    @Override
    public T call() throws Exception {
        if (throttleCheck.checkNoInterrupt()) {
            try {
                throttledValue = ImmutableFuture.of(callable.call());
            } catch (final Throwable t) {
                throttledValue = ThrowableFuture.of(t);
            }
        }
        try {
            return throttledValue.get();
        } catch (final ExecutionException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    public static <T> ThrottledCallable<T> of(final Duration throttleInterval, final Callable<T> callable) {
        return new ThrottledCallable<>(throttleInterval, callable);
    }

}
