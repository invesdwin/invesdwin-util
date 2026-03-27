package de.invesdwin.util.collections.array.large.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class ShiftedIndexDelegateLargeBitSet implements ILargeBitSet {

    private final ILargeBitSet delegate;
    private final long subtractFromIndex;

    private ShiftedIndexDelegateLargeBitSet(final ILargeBitSet delegate, final long subtractFromIndex) {
        this.delegate = delegate;
        this.subtractFromIndex = subtractFromIndex;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    private long adjustIndex(final long index) {
        return index - subtractFromIndex;
    }

    private long unadjustIndex(final long index) {
        if (index >= ISkippingLargeIndexProvider.END) {
            return index;
        } else {
            return index + subtractFromIndex;
        }
    }

    @Override
    public void add(final long index) {
        delegate.add(adjustIndex(index));
    }

    @Override
    public void remove(final long index) {
        delegate.remove(adjustIndex(index));
    }

    @Override
    public boolean contains(final long index) {
        return delegate.contains(adjustIndex(index));
    }

    @Override
    public void flip(final long index) {
        delegate.flip(adjustIndex(index));
    }

    @Override
    public void flip(final long index, final long length) {
        delegate.flip(adjustIndex(index), length);
    }

    @Override
    public ILargeBitSet optimize() {
        final ILargeBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else {
            return new ShiftedIndexDelegateLargeBitSet(optimized, subtractFromIndex);
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public ILargeBitSet negate() {
        return of(delegate.negate(), subtractFromIndex);
    }

    @Override
    public ILargeBitSet negateShallow() {
        return of(delegate.negateShallow(), subtractFromIndex);
    }

    @Override
    public long getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public long size() {
        return delegate.size() - subtractFromIndex;
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final ISkippingLargeIndexProvider provider = delegate.newSkippingIndexProvider();
        if (provider == null) {
            return null;
        } else {
            return key -> unadjustIndex(provider.next(adjustIndex(key)));
        }
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public ILargeBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static ILargeBitSet of(final ILargeBitSet delegate, final long subtractFromIndex) {
        if (subtractFromIndex == 0) {
            return delegate;
        } else {
            return new ShiftedIndexDelegateLargeBitSet(delegate, subtractFromIndex);
        }
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
        delegate.clear();
    }

    @Override
    public void clear(final long index, final long length) {
        delegate.clear(adjustIndex(index), length);
    }

}
