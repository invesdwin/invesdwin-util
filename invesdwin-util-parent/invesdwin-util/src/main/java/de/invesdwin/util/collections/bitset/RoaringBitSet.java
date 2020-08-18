package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.RoaringBitmap;

@NotThreadSafe
public class RoaringBitSet implements IBitSet {

    private final RoaringBitmap bitSet;

    public RoaringBitSet() {
        this.bitSet = new RoaringBitmap();
    }

    @Override
    public void add(final int index) {
        bitSet.add(index);
    }

    @Override
    public void remove(final int index) {
        bitSet.remove(index);
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.contains(index);
    }

    @Override
    public void optimize() {
        bitSet.trim();
        bitSet.runOptimize();
    }

}
