package de.invesdwin.util.collections.array.primitive.bitset.concurrent;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class SynchronizedPrimitiveBitSet implements IPrimitiveBitSet {

    private final IPrimitiveBitSet delegate;
    private final Object lock;

    public SynchronizedPrimitiveBitSet(final IPrimitiveBitSet delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedPrimitiveBitSet(final IPrimitiveBitSet delegate, final Object lock) {
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
    public void flip(final int index) {
        synchronized (lock) {
            delegate.flip(index);
        }
    }

    @Override
    public void flip(final int index, final int length) {
        synchronized (lock) {
            delegate.flip(index, length);
        }
    }

    @Override
    public IPrimitiveBitSet optimize() {
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
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        synchronized (lock) {
            return delegate.and(others);
        }
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        synchronized (lock) {
            return delegate.andRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        synchronized (lock) {
            return delegate.or(others);
        }
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        synchronized (lock) {
            return delegate.orRange(fromInclusive, toExclusive, others);
        }
    }

    @Override
    public IPrimitiveBitSet negate() {
        synchronized (lock) {
            return delegate.negate();
        }
    }

    /**
     * Since we still operate on the underlying bitset, we reuse the lock here.
     */
    @Override
    public IPrimitiveBitSet negateShallow() {
        synchronized (lock) {
            return new SynchronizedPrimitiveBitSet(delegate.negateShallow(), lock);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        final ISkippingPrimitiveIndexProvider f;
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
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        synchronized (lock) {
            delegate.getBooleans(srcPos, dest, destPos, length);
        }
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        synchronized (lock) {
            return delegate.unwrap();
        }
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        synchronized (lock) {
            return delegate.getBuffer(buffer);
        }
    }

    @Override
    public int getBufferLength() {
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
    public void clear(final int index, final int length) {
        synchronized (lock) {
            delegate.clear(index, length);
        }
    }

}
