package de.invesdwin.util.concurrent.priority;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;

@Immutable
public class PriorityFuture<T> implements RunnableFuture<T>, IPriorityProvider {

    public static final Comparator<Runnable> COMPARATOR = new Comparator<Runnable>() {
        @Override
        public int compare(final Runnable o1, final Runnable o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                final double p1 = ((IPriorityProvider) o1).getPriority();
                final double p2 = ((IPriorityProvider) o2).getPriority();
                final boolean p1NaN = Doubles.isNaN(p1);
                final boolean p2NaN = Doubles.isNaN(p2);
                if (p1NaN && p2NaN) {
                    return 0;
                } else if (p1NaN) {
                    return 1;
                } else if (p2NaN) {
                    return -1;
                } else {
                    return Doubles.compare(p1, p2);
                }
            }
        }
    };

    private final RunnableFuture<T> src;
    private final double priority;

    public PriorityFuture(final RunnableFuture<T> other, final double priority) {
        this.src = other;
        this.priority = priority;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return src.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return src.isCancelled();
    }

    @Override
    public boolean isDone() {
        return src.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return src.get();
    }

    @Override
    public T get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return src.get();
    }

    @Override
    public void run() {
        src.run();
    }
}