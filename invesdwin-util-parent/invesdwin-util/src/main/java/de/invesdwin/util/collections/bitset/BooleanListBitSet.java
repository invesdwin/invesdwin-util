package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

@NotThreadSafe
public class BooleanListBitSet implements IBitSet {

    private final BooleanList bitSet;
    private int trueCount = 0;

    public BooleanListBitSet() {
        this.bitSet = new BooleanArrayList();
    }

    public BooleanListBitSet(final int expectedSize) {
        this.bitSet = new BooleanArrayList(expectedSize);
    }

    @Override
    public void add(final int index) {
        while (bitSet.size() <= index) {
            bitSet.add(false);
        }
        bitSet.set(index, true);
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        if (bitSet.size() < index) {
            bitSet.set(index, false);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final int index) {
        if (bitSet.size() <= index) {
            return false;
        } else {
            return bitSet.getBoolean(index);
        }
    }

    @Override
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            return this;
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
    public int getTrueCount() {
        return trueCount;
    }

    @Override
    public boolean isEmpty() {
        return trueCount == 0;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return null;
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

}
