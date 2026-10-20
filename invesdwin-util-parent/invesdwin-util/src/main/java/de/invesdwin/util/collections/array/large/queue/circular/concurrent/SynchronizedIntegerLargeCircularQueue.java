package de.invesdwin.util.collections.array.large.queue.circular.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.large.queue.IIntegerLargeCircularQueue;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Serializes all {@link IIntegerLargeCircularQueue} operations with a monitor ({@code synchronized}); clamped reads and the window copy take the lock
 * once for the whole operation. The disabled factory returns the raw unlocked implementation instead, so headless
 * backtests pay no locking at all.
 * Monitor instead of ILock (Edwin): better stack traces under contention (the owning thread is
 * visible without lock-tracing), no extra lock object; these wrappers are LEAF locks (they never
 * acquire a second lock inside a method) so they can never take part in a lock cycle - cycle
 * detection stays available where nesting exists, on the engine-side series locks via Locks.
 */
@ThreadSafe
public class SynchronizedIntegerLargeCircularQueue implements IIntegerLargeCircularQueue {

    private final IIntegerLargeCircularQueue delegate;
    private final Object lock;

    public SynchronizedIntegerLargeCircularQueue(final IIntegerLargeCircularQueue delegate) {
        this.delegate = delegate;
        //shared monitor default: the wrapper itself, like SynchronizedMap/SynchronizedPrimitiveBitSet
        this.lock = this;
    }

    public SynchronizedIntegerLargeCircularQueue(final IIntegerLargeCircularQueue delegate, final Object lock) {
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
    public int get(final long index) {
        synchronized (lock) {
            return delegate.get(index);
        }
    }

    @Override
    public int getReverse(final long index) {
        synchronized (lock) {
            return delegate.getReverse(index);
        }
    }

    @Override
    public int getOrMissing(final long index) {
        synchronized (lock) {
            return delegate.getOrMissing(index);
        }
    }

    @Override
    public int getReverseOrMissing(final long index) {
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
    public void circularAdd(final int value) {
        synchronized (lock) {
            delegate.circularAdd(value);
        }
    }

    @Override
    public void circularPrepend(final int value) {
        synchronized (lock) {
            delegate.circularPrepend(value);
        }
    }

    @Override
    public int evictAdd(final int value) {
        synchronized (lock) {
            return delegate.evictAdd(value);
        }
    }

    @Override
    public int evictPrepend(final int value) {
        synchronized (lock) {
            return delegate.evictPrepend(value);
        }
    }

    @Override
    public boolean offer(final int value) {
        synchronized (lock) {
            return delegate.offer(value);
        }
    }

    @Override
    public int poll() {
        synchronized (lock) {
            return delegate.poll();
        }
    }

    @Override
    public int peek() {
        synchronized (lock) {
            return delegate.peek();
        }
    }

    @Override
    public int remove() {
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
    public int element() {
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
    public int getIntegers(final int[] dst, final int dstOffset, final long srcIndex, final int length) {
        synchronized (lock) {
            return delegate.getIntegers(dst, dstOffset, srcIndex, length);
        }
    }
}
