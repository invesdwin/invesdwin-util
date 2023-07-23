package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class ShiftedIndexDelegateBitSet implements IBitSet {

    private final IBitSet delegate;
    private final int subtractFromIndex;

    private ShiftedIndexDelegateBitSet(final IBitSet delegate, final int subtractFromIndex) {
        this.delegate = delegate;
        this.subtractFromIndex = subtractFromIndex;
    }

    private int adjustIndex(final int index) {
        return index - subtractFromIndex;
    }

    private int unadjustIndex(final int index) {
        if (index >= ISkippingIndexProvider.END) {
            return index;
        } else {
            return index + subtractFromIndex;
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
            return new ShiftedIndexDelegateBitSet(optimized, subtractFromIndex);
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
        return of(delegate.negate(), subtractFromIndex);
    }

    @Override
    public IBitSet negateShallow() {
        return of(delegate.negateShallow(), subtractFromIndex);
    }

    @Override
    public int getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public int getExpectedSize() {
        return delegate.getExpectedSize() - subtractFromIndex;
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
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static IBitSet of(final IBitSet delegate, final int subtractFromIndex) {
        if (subtractFromIndex == 0) {
            return delegate;
        } else {
            return new ShiftedIndexDelegateBitSet(delegate, subtractFromIndex);
        }
    }

}
