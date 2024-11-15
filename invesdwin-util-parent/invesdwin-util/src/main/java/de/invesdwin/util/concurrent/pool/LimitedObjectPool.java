package de.invesdwin.util.concurrent.pool;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class LimitedObjectPool<E> implements IObjectPool<E> {

    private final IObjectPool<E> delegate;
    private final int maximumSize;
    private final AtomicInteger leasedInstances = new AtomicInteger();
    private final ASpinWait leasedWait = new ASpinWait() {
        @Override
        public boolean isConditionFulfilled() throws Exception {
            return leasedInstances.get() < maximumSize;
        }
    };
    private final Duration timeout;

    public LimitedObjectPool(final IObjectPool<E> delegate, final int maximumSize, final Duration timeout) {
        this.delegate = delegate;
        this.maximumSize = maximumSize;
        this.timeout = timeout;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    @Override
    public E borrowObject() {
        try {
            final long waitingSinceNanos = System.nanoTime();
            while (true) {
                if (leasedWait.awaitFulfill(waitingSinceNanos)) {
                    //make sure that maximum size can not be exceeded by parallel leasing
                    synchronized (this) {
                        if (leasedWait.isConditionFulfilled()) {
                            leasedInstances.incrementAndGet();
                            return delegate.borrowObject();
                        }
                    }
                } else {
                    final long curNanos = System.nanoTime();
                    if (timeout.isGreaterThan(curNanos - waitingSinceNanos, FTimeUnit.NANOSECONDS)) {
                        throw new TimeoutException("Timeout exceeded: " + timeout);
                    }
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnObject(final E element) {
        if (element != null) {
            delegate.returnObject(element);
            leasedInstances.decrementAndGet();
        }
    }

    @Override
    public void invalidateObject(final E element) {
        if (element != null) {
            delegate.invalidateObject(element);
            leasedInstances.decrementAndGet();
        }
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        final int lSize = leasedInstances.get();
        final int dSize = delegate.size();
        return lSize + dSize;
    }

}
