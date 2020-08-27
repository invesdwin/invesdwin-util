package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BooleanArrayBitSet implements IBitSet {

    private final boolean[] bitSet;

    public BooleanArrayBitSet(final int expectedSize) {
        this.bitSet = new boolean[expectedSize];
    }

    @Override
    public void add(final int index) {
        bitSet[index] = true;
    }

    @Override
    public void remove(final int index) {
        bitSet[index] = false;
    }

    @Override
    public boolean contains(final int index) {
        return bitSet[index];
    }

    @Override
    public void optimize() {
        //noop
    }

}
