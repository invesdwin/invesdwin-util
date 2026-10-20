package de.invesdwin.util.collections.array.primitive.queue.circular.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveCircularQueue;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Serializes all {@link IDoublePrimitiveCircularQueue} operations with an {@link ILock}; clamped reads and the window copy
 * take the lock once for the whole operation; remove()/element() are overridden to keep the
 * check-then-act atomic under a single lock acquisition (the inherited interface defaults would
 * take two).
 * ILock-based variant, kept beside the monitor-based Synchronized* siblings exactly like the
 * platform's LockedMap sits beside SynchronizedMap: use it when this lock needs Locks features
 * (cycle detection / wait timeouts when enabled via Locks) or when one ILock is shared across
 * structures (series-level seam). The synchronized factory's newCircular* methods return the
 * monitor-based Synchronized* variants by default - there are no factory wrap methods for
 * queues (unlike externally provided maps/sets these are always factory-created), so construct
 * this class directly around a queue when an ILock guard is wanted.
 */
@ThreadSafe
public class LockedDoublePrimitiveCircularQueue implements IDoublePrimitiveCircularQueue {

    private final IDoublePrimitiveCircularQueue delegate;
    private final ILock lock;

    public LockedDoublePrimitiveCircularQueue(final IDoublePrimitiveCircularQueue delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedDoublePrimitiveCircularQueue(final IDoublePrimitiveCircularQueue delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isFull() {
        lock.lock();
        try {
            return delegate.isFull();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        lock.lock();
        try {
            return delegate.capacity();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isInRange(final int index) {
        lock.lock();
        try {
            return delegate.isInRange(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isInRangeReverse(final int index) {
        lock.lock();
        try {
            return delegate.isInRangeReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double get(final int index) {
        lock.lock();
        try {
            return delegate.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getReverse(final int index) {
        lock.lock();
        try {
            return delegate.getReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getOrMissing(final int index) {
        lock.lock();
        try {
            return delegate.getOrMissing(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getReverseOrMissing(final int index) {
        lock.lock();
        try {
            return delegate.getReverseOrMissing(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double remove() {
        //atomic check-then-act: the inherited default would test isEmpty() under one
        //lock acquisition and poll() under the next -- race window in between.
        lock.lock();
        try {
            if (delegate.isEmpty()) {
                throw FastNoSuchElementException.getInstance("poll returned nothing");
            }
            return delegate.poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double element() {
        lock.lock();
        try {
            if (delegate.isEmpty()) {
                throw FastNoSuchElementException.getInstance("peek returned nothing");
            }
            return delegate.peek();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pretendAdd() {
        lock.lock();
        try {
            delegate.pretendAdd();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pretendPrepend() {
        lock.lock();
        try {
            delegate.pretendPrepend();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void circularAdd(final double value) {
        lock.lock();
        try {
            delegate.circularAdd(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void circularPrepend(final double value) {
        lock.lock();
        try {
            delegate.circularPrepend(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double evictAdd(final double value) {
        lock.lock();
        try {
            return delegate.evictAdd(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double evictPrepend(final double value) {
        lock.lock();
        try {
            return delegate.evictPrepend(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(final double value) {
        lock.lock();
        try {
            return delegate.offer(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double poll() {
        lock.lock();
        try {
            return delegate.poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double peek() {
        lock.lock();
        try {
            return delegate.peek();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            delegate.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getDoubles(final double[] dst, final int dstOffset, final int srcIndex, final int length) {
        lock.lock();
        try {
            return delegate.getDoubles(dst, dstOffset, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

}
