package de.invesdwin.util.collections.array.primitive.bitset.concurrent;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class LockedPrimitiveBitSet implements IPrimitiveBitSet {

    private final IPrimitiveBitSet delegate;
    private final ILock lock;

    public LockedPrimitiveBitSet(final IPrimitiveBitSet delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedPrimitiveBitSet(final IPrimitiveBitSet delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public int getId() {
        return delegate.getId();
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
    public void flip(final int index) {
        lock.lock();
        try {
            delegate.flip(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void flip(final int index, final int length) {
        lock.lock();
        try {
            delegate.flip(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet optimize() {
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
    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        lock.lock();
        try {
            return delegate.and(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        lock.lock();
        try {
            return delegate.andRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        lock.lock();
        try {
            return delegate.or(others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        lock.lock();
        try {
            return delegate.orRange(fromInclusive, toExclusive, others);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet negate() {
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
    public IPrimitiveBitSet negateShallow() {
        lock.lock();
        try {
            return new LockedPrimitiveBitSet(delegate.negateShallow(), lock);
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
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        final ISkippingPrimitiveIndexProvider f;
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
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        lock.lock();
        try {
            delegate.getBooleans(srcPos, dest, destPos, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        lock.lock();
        try {
            return delegate.unwrap();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        lock.lock();
        try {
            return delegate.getBuffer(buffer);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getBufferLength() {
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
    public void clear(final int index, final int length) {
        lock.lock();
        try {
            delegate.clear(index, length);
        } finally {
            lock.unlock();
        }
    }
}
