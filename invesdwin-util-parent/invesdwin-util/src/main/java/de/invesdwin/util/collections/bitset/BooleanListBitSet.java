package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

@NotThreadSafe
public class BooleanListBitSet implements IBitSet {

    private final BooleanList bitSet;

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
    }

    @Override
    public void remove(final int index) {
        if (bitSet.size() < index) {
            bitSet.set(index, false);
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
    public void optimize() {
        //noop
    }

}
