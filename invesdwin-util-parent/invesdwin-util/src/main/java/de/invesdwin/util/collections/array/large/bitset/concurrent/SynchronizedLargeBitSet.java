package de.invesdwin.util.collections.array.large.bitset.concurrent;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public class SynchronizedLargeBitSet implements ILargeBitSet {

    private final ILargeBitSet delegate;
    private final Object lock;

    public SynchronizedLargeBitSet(final ILargeBitSet delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedLargeBitSet(final ILargeBitSet delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void add(final long index) {
        synchronized (lock) {
            delegate.add(index);
        }
    }

    @Override
    public void remove(final long index) {
        synchronized (lock) {
            delegate.remove(index);
        }
    }

    @Override
    public boolean contains(final long index) {
        synchronized (lock) {
            return delegate.contains(index);
        }
    }

    @Override
    public void flip(final long index) {
        synchronized (lock) {
            delegate.flip(index);
        }
    }

    @Override
    public void flip(final long index, final long length) {
        synchronized (lock) {
            delegate.flip(index, length);
        }
    }

    @Override
    public ILargeBitSet optimize() {
        synchronized (lock) {
            return delegate.optimize();
        }
    }

    @Override
    public long getTrueCount() {
        synchronized (lock) {
            return delegate.getTrueCount();
        }
    }

    @Override
    public long size() {
        synchronized (lock) {
            return delegate.size();
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        synchronized (lock) {
            return delegate.and(others);
        }
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        synchronized (lock) {
            return delegate.andRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        synchronized (lock) {
            return delegate.or(others);
        }
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        synchronized (lock) {
            return delegate.orRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public ILargeBitSet negate() {
        synchronized (lock) {
            return delegate.negate();
        }
    }

    /**
     * Since we still operate on the underlying bitset, we reuse the lock here.
     */
    @Override
    public ILargeBitSet negateShallow() {
        synchronized (lock) {
            return new SynchronizedLargeBitSet(delegate.negateShallow(), lock);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final ISkippingLargeIndexProvider f;
        synchronized (lock) {
            f = delegate.newSkippingIndexProvider();
        }
        if (f == null) {
            return null;
        }
        return nextCandidate -> {
            synchronized (lock) {
                return f.next(nextCandidate);
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        synchronized (lock) {
            delegate.getBooleans(srcPos, dest, destPos, length);
        }
    }

    @Override
    public ILargeBitSet unwrap() {
        synchronized (lock) {
            return delegate.unwrap();
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        synchronized (lock) {
            return delegate.getBuffer(buffer);
        }
    }

    @Override
    public long getBufferLength() {
        synchronized (lock) {
            return delegate.getBufferLength();
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

    @Override
    public void clear(final long index, final long length) {
        synchronized (lock) {
            delegate.clear(index, length);
        }
    }

}
