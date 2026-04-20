package de.invesdwin.util.collections.array.large.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.EmptyLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.PeekingSkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class ShallowNegatedLargeBitSet implements ILargeBitSet {

    private final ILargeBitSet delegate;
    private final long trueCount;

    public ShallowNegatedLargeBitSet(final ILargeBitSet delegate) {
        this.delegate = delegate;
        this.trueCount = delegate.size() - delegate.getTrueCount();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void add(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final long index) {
        return !delegate.contains(index);
    }

    @Override
    public void flip(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flip(final long index, final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet optimize() {
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        final ILargeBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else if (optimized.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        } else {
            return new ShallowNegatedLargeBitSet(optimized);
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet negate() {
        return delegate;
    }

    @Override
    public ILargeBitSet negateShallow() {
        return delegate;
    }

    @Override
    public long getTrueCount() {
        return trueCount;
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return trueCount == 0;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final PeekingSkippingLargeIndexProvider provider = new PeekingSkippingLargeIndexProvider(
                delegate.newSkippingIndexProvider());
        return nextCandidate -> {
            long nextFalseCandidate = nextCandidate;
            while (true) {
                final long nextTrueValue = provider.peek(nextFalseCandidate);
                if (nextTrueValue > nextFalseCandidate) {
                    return nextFalseCandidate;
                } else {
                    nextFalseCandidate++;
                }
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet unwrap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(final long index, final long length) {
        throw new UnsupportedOperationException();
    }

}
