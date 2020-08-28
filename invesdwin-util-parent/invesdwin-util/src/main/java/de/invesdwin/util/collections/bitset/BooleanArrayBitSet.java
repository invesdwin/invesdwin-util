package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BooleanArrayBitSet implements IBitSet {

    private final boolean[] bitSet;
    private int trueCount = 0;

    public BooleanArrayBitSet(final int expectedSize) {
        this.bitSet = new boolean[expectedSize];
    }

    @Override
    public void add(final int index) {
        bitSet[index] = true;
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        bitSet[index] = false;
        trueCount--;
    }

    @Override
    public boolean contains(final int index) {
        return bitSet[index];
    }

    @Override
    public void optimize() {
        //noop
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTrueCount() {
        return trueCount;
    }

    @Override
    public boolean isEmpty() {
        return trueCount > 0;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return null;
    }

}
