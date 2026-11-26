package de.invesdwin.util.collections.array.large.queue.circular.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Serializes all {@link ILongLargeCircularQueue} operations with a monitor ({@code synchronized}); clamped reads and
 * the window copy take the lock once for the whole operation. The disabled factory returns the raw unlocked
 * implementation instead, so headless backtests pay no locking at all. Monitor instead of ILock (Edwin): better stack
 * traces under contention (the owning thread is visible without lock-tracing), no extra lock object; these wrappers are
 * LEAF locks (they never acquire a second lock inside a method) so they can never take part in a lock cycle - cycle
 * detection stays available where nesting exists, on the engine-side series locks via Locks.
 */
@ThreadSafe
public class SynchronizedLongLargeCircularQueue implements ILongLargeCircularQueue {

    private final ILongLargeCircularQueue delegate;
    private final Object lock;

    public SynchronizedLongLargeCircularQueue(final ILongLargeCircularQueue delegate) {
        this.delegate = delegate;
        //shared monitor default: the wrapper itself, like SynchronizedMap/SynchronizedPrimitiveBitSet
        this.lock = this;
    }

    public SynchronizedLongLargeCircularQueue(final ILongLargeCircularQueue delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public long size() {
        synchronized (lock) {
            return delegate.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public boolean isFull() {
        synchronized (lock) {
            return delegate.isFull();
        }
    }

    @Override
    public long capacity() {
        synchronized (lock) {
            return delegate.capacity();
        }
    }

    @Override
    public boolean isInRange(final long index) {
        synchronized (lock) {
            return delegate.isInRange(index);
        }
    }

    @Override
    public boolean isInRangeReverse(final long index) {
        synchronized (lock) {
            return delegate.isInRangeReverse(index);
        }
    }

    @Override
    public long get(final long index) {
        synchronized (lock) {
            return delegate.get(index);
        }
    }

    @Override
    public long getReverse(final long index) {
        synchronized (lock) {
            return delegate.getReverse(index);
        }
    }

    @Override
    public long getOrMissing(final long index) {
        synchronized (lock) {
            return delegate.getOrMissing(index);
        }
    }

    @Override
    public long getReverseOrMissing(final long index) {
        synchronized (lock) {
            return delegate.getReverseOrMissing(index);
        }
    }

    @Override
    public void pretendAdd() {
        synchronized (lock) {
            delegate.pretendAdd();
        }
    }

    @Override
    public void pretendPrepend() {
        synchronized (lock) {
            delegate.pretendPrepend();
        }
    }

    @Override
    public void circularAdd(final long value) {
        synchronized (lock) {
            delegate.circularAdd(value);
        }
    }

    @Override
    public void circularPrepend(final long value) {
        synchronized (lock) {
            delegate.circularPrepend(value);
        }
    }

    @Override
    public long evictAdd(final long value) {
        synchronized (lock) {
            return delegate.evictAdd(value);
        }
    }

    @Override
    public long evictPrepend(final long value) {
        synchronized (lock) {
            return delegate.evictPrepend(value);
        }
    }

    @Override
    public boolean offer(final long value) {
        synchronized (lock) {
            return delegate.offer(value);
        }
    }

    @Override
    public long poll() {
        synchronized (lock) {
            return delegate.poll();
        }
    }

    @Override
    public long peek() {
        synchronized (lock) {
            return delegate.peek();
        }
    }

    @Override
    public long remove() {
        //atomic check-then-act: the inherited default would test isEmpty() under one
        //lock acquisition and poll() under the next -- race window in between.
        synchronized (lock) {
            if (delegate.isEmpty()) {
                throw FastNoSuchElementException.getInstance("poll returned nothing");
            }
            return delegate.poll();
        }
    }

    @Override
    public long element() {
        synchronized (lock) {
            if (delegate.isEmpty()) {
                throw FastNoSuchElementException.getInstance("peek returned nothing");
            }
            return delegate.peek();
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

    @Override
    public int getLongs(final long[] dst, final int dstOffset, final long srcIndex, final int length) {
        synchronized (lock) {
            return delegate.getLongs(dst, dstOffset, srcIndex, length);
        }
    }
}
