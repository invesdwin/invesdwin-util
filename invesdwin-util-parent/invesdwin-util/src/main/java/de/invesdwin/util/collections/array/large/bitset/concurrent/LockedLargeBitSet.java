package de.invesdwin.util.collections.array.large.bitset.concurrent;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public class LockedLargeBitSet implements ILargeBitSet {

    private final ILargeBitSet delegate;
    private final ILock lock;

    public LockedLargeBitSet(final ILargeBitSet delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedLargeBitSet(final ILargeBitSet delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void add(final long index) {
        lock.lock();
        try {
            delegate.add(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(final long index) {
        lock.lock();
        try {
            delegate.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final long index) {
        lock.lock();
        try {
            return delegate.contains(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void flip(final long index) {
        lock.lock();
        try {
            delegate.flip(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void flip(final long index, final long length) {
        lock.lock();
        try {
            delegate.flip(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet optimize() {
        lock.lock();
        try {
            return delegate.optimize();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getTrueCount() {
        lock.lock();
        try {
            return delegate.getTrueCount();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        lock.lock();
        try {
            return delegate.and(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        lock.lock();
        try {
            return delegate.andRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        lock.lock();
        try {
            return delegate.or(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        lock.lock();
        try {
            return delegate.orRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet negate() {
        lock.lock();
        try {
            return delegate.negate();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Since we still operate on the underlying bitset, we reuse the lock here.
     */
    @Override
    public ILargeBitSet negateShallow() {
        lock.lock();
        try {
            return new LockedLargeBitSet(delegate.negateShallow(), lock);
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
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final ISkippingLargeIndexProvider f;
        lock.lock();
        try {
            f = delegate.newSkippingIndexProvider();
        } finally {
            lock.unlock();
        }
        if (f == null) {
            return null;
        }
        return nextCandidate -> {
            lock.lock();
            try {
                return f.next(nextCandidate);
            } finally {
                lock.unlock();
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        lock.lock();
        try {
            delegate.getBooleans(srcPos, dest, destPos, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ILargeBitSet unwrap() {
        lock.lock();
        try {
            return delegate.unwrap();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        lock.lock();
        try {
            return delegate.getBuffer(buffer);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getBufferLength() {
        lock.lock();
        try {
            return delegate.getBufferLength();
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
    public void clear(final long index, final long length) {
        lock.lock();
        try {
            delegate.clear(index, length);
        } finally {
            lock.unlock();
        }
    }

}
