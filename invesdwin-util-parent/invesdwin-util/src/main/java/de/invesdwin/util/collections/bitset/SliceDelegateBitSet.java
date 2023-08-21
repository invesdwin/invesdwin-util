package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class SliceDelegateBitSet implements IBitSet {

    private final IBitSet delegate;
    private final int from;
    private final int length;

    private SliceDelegateBitSet(final IBitSet delegate, final int from, final int length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    private int adjustIndex(final int index) {
        return index + from;
    }

    private int unadjustIndex(final int index) {
        if (index >= ISkippingIndexProvider.END) {
            return index;
        } else {
            return index - from;
        }
    }

    @Override
    public void add(final int index) {
        delegate.add(adjustIndex(index));
    }

    @Override
    public void remove(final int index) {
        delegate.remove(adjustIndex(index));
    }

    @Override
    public boolean contains(final int index) {
        return delegate.contains(adjustIndex(index));
    }

    @Override
    public IBitSet optimize() {
        final IBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else {
            return new SliceDelegateBitSet(optimized, from, length);
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IBitSet negate() {
        return of(delegate.negate(), from, length);
    }

    @Override
    public IBitSet negateShallow() {
        return of(delegate.negateShallow(), from, length);
    }

    @Override
    public int getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public int size() {
        return Integers.min(delegate.size() - from, length);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        final ISkippingIndexProvider provider = delegate.newSkippingIndexProvider();
        if (provider == null) {
            return null;
        } else {
            return key -> unadjustIndex(provider.next(adjustIndex(key)));
        }
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        delegate.getBooleans(srcPos + from, dest, destPos, length);
    }

    @Override
    public IBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static IBitSet of(final IBitSet delegate, final int from, final int length) {
        if (from == 0 && length == delegate.size()) {
            return delegate;
        } else {
            return new SliceDelegateBitSet(delegate, from, length);
        }
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

}
