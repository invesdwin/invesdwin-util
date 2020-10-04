package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ShallowNegatedBitSet implements IBitSet {

    private final IBitSet delegate;
    private final int trueCount;

    public ShallowNegatedBitSet(final IBitSet delegate) {
        this.delegate = delegate;
        this.trueCount = delegate.getExpectedSize() - delegate.getTrueCount();
    }

    @Override
    public void add(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final int index) {
        return !delegate.contains(index);
    }

    @Override
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        }
        final IBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else if (optimized.isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            return new ShallowNegatedBitSet(optimized);
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet negate() {
        return delegate;
    }

    @Override
    public IBitSet negateShallow() {
        return delegate;
    }

    @Override
    public int getTrueCount() {
        return trueCount;
    }

    @Override
    public int getExpectedSize() {
        return delegate.getExpectedSize();
    }

    @Override
    public boolean isEmpty() {
        return trueCount == 0;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        final PeekingSkippingIndexProvider provider = new PeekingSkippingIndexProvider(
                delegate.newSkippingIndexProvider());
        return nextCandidate -> {
            int nextFalseCandidate = nextCandidate;
            while (true) {
                final int nextTrueValue = provider.peek(nextFalseCandidate);
                if (nextTrueValue > nextFalseCandidate) {
                    return nextFalseCandidate;
                } else {
                    nextFalseCandidate++;
                }
            }
        };
    }

    @Override
    public IBitSet unwrap() {
        throw new UnsupportedOperationException();
    }

}
