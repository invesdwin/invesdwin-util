package de.invesdwin.util.collections.array.large.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class SliceDelegateLargeBitSet implements ILargeBitSet {

    private final ILargeBitSet delegate;
    private final long from;
    private final long length;

    private SliceDelegateLargeBitSet(final ILargeBitSet delegate, final long from, final long length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    private long adjustIndex(final long index) {
        return index + from;
    }

    private long unadjustIndex(final long index) {
        if (index >= ISkippingLargeIndexProvider.END) {
            return index;
        } else {
            return index - from;
        }
    }

    private void assertLength(final long index, final long length) {
        final long remaining = this.length - index;
        if (length > remaining) {
            throw FastIndexOutOfBoundsException.getInstance(
                    "Length [%s] exceeds remaining length [%s] at index [%s] and total length [%s]", length, remaining,
                    index, this.length);
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
        assertLength(index, length);
        delegate.flip(adjustIndex(index), length);
    }

    @Override
    public ILargeBitSet optimize() {
        final ILargeBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else {
            return new SliceDelegateLargeBitSet(optimized, from, length);
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
        return of(delegate.negate(), from, length);
    }

    @Override
    public ILargeBitSet negateShallow() {
        return of(delegate.negateShallow(), from, length);
    }

    @Override
    public long getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public long size() {
        return Longs.min(delegate.size() - from, length);
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
        assertLength(srcPos, length);
        delegate.getBooleans(srcPos + from, dest, destPos, length);
    }

    @Override
    public ILargeBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static ILargeBitSet of(final ILargeBitSet delegate, final long from, final long length) {
        if (from == 0 && length == delegate.size()) {
            return delegate;
        } else {
            return new SliceDelegateLargeBitSet(delegate, from, length);
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
        clear(from, length);
    }

    @Override
    public void clear(final long index, final long length) {
        assertLength(index, length);
        delegate.clear(adjustIndex(index), length);
    }

}
