package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

@NotThreadSafe
public class RoaringBitSet implements IBitSet {

    private final RoaringBitmap bitSet;
    private int trueCount = 0;

    public RoaringBitSet(final RoaringBitmap bitSet) {
        this.bitSet = bitSet;
        this.trueCount = -1;
    }

    public RoaringBitSet() {
        this.bitSet = new RoaringBitmap();
    }

    @Override
    public void add(final int index) {
        if (bitSet.checkedAdd(index)) {
            trueCount++;
        }
    }

    @Override
    public void remove(final int index) {
        if (bitSet.checkedRemove(index)) {
            trueCount--;
        }
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

    @Override
    public IBitSet and(final IBitSet... others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final RoaringBitSet cOther = (RoaringBitSet) others[i];
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = FastAggregation.workShyAnd(new long[1024], cOthers);
        return new RoaringBitSet(combined);
    }

    @Override
    public int getTrueCount() {
        if (trueCount == -1) {
            trueCount = bitSet.getCardinality();
        }
        return trueCount;
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    public RoaringBitmap getBitSet() {
        return bitSet;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        final PeekableIntIterator delegate = bitSet.getIntIterator();
        return cur -> {
            if (!delegate.hasNext()) {
                return Integer.MAX_VALUE;
            }
            int next = delegate.next();
            while (next <= cur) {
                if (!delegate.hasNext()) {
                    return Integer.MAX_VALUE;
                }
                next = delegate.next();
            }
            return next;
        };
    }

}
