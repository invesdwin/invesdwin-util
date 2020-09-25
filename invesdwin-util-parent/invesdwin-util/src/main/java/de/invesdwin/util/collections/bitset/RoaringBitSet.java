package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;

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
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            bitSet.trim();
            bitSet.runOptimize();
            return this;
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final RoaringBitSet cOther = (RoaringBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = FastAggregation.and(cOthers);
        return new RoaringBitSet(combined);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final RoaringBitSet cOther = (RoaringBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = RoaringBitmap.and(new ArrayCloseableIterator<RoaringBitmap>(cOthers),
                fromInclusive, (long) toExclusive);
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
        return nextCandidate -> {
            delegate.advanceIfNeeded(nextCandidate);
            int next;
            do {
                if (!delegate.hasNext()) {
                    return ISkippingIndexProvider.END;
                }
                next = delegate.next();
            } while (next < nextCandidate);
            return next;
        };
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

}
