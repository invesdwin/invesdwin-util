package de.invesdwin.util.concurrent.pool;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.loop.ASpinWait;

@ThreadSafe
public class LimitedObjectPool<E> implements IObjectPool<E> {

    private final IObjectPool<E> delegate;
    private final int maximumSize;
    private final AtomicInteger leasedInstances = new AtomicInteger();
    private final ASpinWait leasedWait = new ASpinWait() {

        @Override
        protected boolean isSpinAllowed(final long waitingSinceNanos) {
            return false;
        }

        @Override
        public boolean isConditionFulfilled() throws Exception {
            return leasedInstances.get() < maximumSize;
        }
    };

    public LimitedObjectPool(final IObjectPool<E> delegate, final int maximumSize) {
        this.delegate = delegate;
        this.maximumSize = maximumSize;
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
    public void clear() {
        delegate.clear();
    }

}
