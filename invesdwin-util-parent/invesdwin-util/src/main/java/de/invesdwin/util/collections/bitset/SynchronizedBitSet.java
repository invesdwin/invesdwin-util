package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class SynchronizedBitSet implements IBitSet {

    private final IBitSet delegate;
    private final Object lock;

    public SynchronizedBitSet(final IBitSet delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedBitSet(final IBitSet delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void add(final int index) {
        synchronized (lock) {
            delegate.add(index);
        }
    }

    @Override
    public void remove(final int index) {
        synchronized (lock) {
            delegate.remove(index);
        }
    }

    @Override
    public boolean contains(final int index) {
        synchronized (lock) {
            return delegate.contains(index);
        }
    }

    @Override
    public IBitSet optimize() {
        synchronized (lock) {
            return delegate.optimize();
        }
    }

    @Override
    public int getTrueCount() {
        synchronized (lock) {
            return delegate.getTrueCount();
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            return delegate.size();
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        synchronized (lock) {
            return delegate.and(others);
        }
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        synchronized (lock) {
            return delegate.andRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        synchronized (lock) {
            return delegate.or(others);
        }
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        synchronized (lock) {
            return delegate.orRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public IBitSet negate() {
        synchronized (lock) {
            return delegate.negate();
        }
    }

    /**
     * Since we still operate on the underlying bitset, we reuse the lock here.
     */
    @Override
    public IBitSet negateShallow() {
        synchronized (lock) {
            return new SynchronizedBitSet(delegate.negateShallow(), lock);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        final ISkippingIndexProvider f;
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
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        synchronized (lock) {
            delegate.getBooleans(srcPos, dest, destPos, length);
        }
    }

    @Override
    public IBitSet unwrap() {
        synchronized (lock) {
            return delegate.unwrap();
        }
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

}
