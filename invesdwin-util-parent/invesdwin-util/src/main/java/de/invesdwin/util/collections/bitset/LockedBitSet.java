package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public class LockedBitSet implements IBitSet {

    private final IBitSet delegate;
    private final ILock lock;

    public LockedBitSet(final IBitSet delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedBitSet(final IBitSet delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public void add(final int index) {
        lock.lock();
        try {
            delegate.add(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(final int index) {
        lock.lock();
        try {
            delegate.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final int index) {
        lock.lock();
        try {
            return delegate.contains(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet optimize() {
        lock.lock();
        try {
            return delegate.optimize();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getTrueCount() {
        lock.lock();
        try {
            return delegate.getTrueCount();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getExpectedSize() {
        lock.lock();
        try {
            return delegate.getExpectedSize();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        lock.lock();
        try {
            return delegate.and(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        lock.lock();
        try {
            return delegate.andRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        lock.lock();
        try {
            return delegate.or(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        lock.lock();
        try {
            return delegate.orRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IBitSet negate() {
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
    public IBitSet negateShallow() {
        lock.lock();
        try {
            return new LockedBitSet(delegate.negateShallow(), lock);
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
    public ISkippingIndexProvider newSkippingIndexProvider() {
        final ISkippingIndexProvider f;
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
    public IBitSet unwrap() {
        lock.lock();
        try {
            return delegate.unwrap();
        } finally {
            lock.unlock();
        }
    }

}
