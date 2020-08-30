package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyBitSet implements IBitSet {

    public static final EmptyBitSet INSTANCE = new EmptyBitSet();

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
        return false;
    }

    @Override
    public IBitSet optimize() {
        return this;
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        return INSTANCE;
    }

    @Override
    public int getTrueCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> ISkippingIndexProvider.END;
    }

}
