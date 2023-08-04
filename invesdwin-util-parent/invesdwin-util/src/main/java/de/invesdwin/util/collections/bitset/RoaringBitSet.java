package de.invesdwin.util.collections.bitset;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class RoaringBitSet implements IBitSet {

    private final RoaringBitmap bitSet;
    private final int expectedSize;
    private int trueCount = 0;

    public RoaringBitSet(final RoaringBitmap bitSet, final int expectedSize) {
        this.bitSet = bitSet;
        this.expectedSize = expectedSize;
        this.trueCount = -1;
    }

    public RoaringBitSet(final int expectedSize) {
        this.bitSet = new RoaringBitmap();
        this.expectedSize = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
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
        return new RoaringBitSet(combined, expectedSize);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
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
        return new RoaringBitSet(combined, expectedSize);
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final RoaringBitSet cOther = (RoaringBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = FastAggregation.or(cOthers);
        return new RoaringBitSet(combined, expectedSize);
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final RoaringBitSet cOther = (RoaringBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = RoaringBitmap.or(new ArrayCloseableIterator<RoaringBitmap>(cOthers),
                fromInclusive, (long) toExclusive);
        return new RoaringBitSet(combined, expectedSize);
    }

    @Override
    public IBitSet negate() {
        final RoaringBitmap negated = bitSet.clone();
        negated.flip(0L, expectedSize);
        return new RoaringBitSet(negated, expectedSize);
    }

    @Override
    public IBitSet negateShallow() {
        return new ShallowNegatedBitSet(this);
    }

    @Override
    public int getTrueCount() {
        if (trueCount == -1) {
            trueCount = bitSet.getCardinality();
        }
        return trueCount;
    }

    @Override
    public int getExpectedSize() {
        return expectedSize;
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
        return new ISkippingIndexProvider() {

            private int cur = -1;

            @Override
            public int next(final int nextCandidate) {
                if (cur == nextCandidate) {
                    return nextCandidate;
                }
                delegate.advanceIfNeeded(nextCandidate);
                do {
                    if (!delegate.hasNext()) {
                        return ISkippingIndexProvider.END;
                    }
                    cur = delegate.next();
                } while (cur < nextCandidate);
                return cur;
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        if (srcPos == destPos) {
            dest.andRange(destPos, destPos + length, new IBitSet[] { this });
        } else {
            final RoaringBitSet cValues = (RoaringBitSet) dest;
            final int target = destPos + length;
            cValues.bitSet.remove(destPos, target);
            final PeekableIntIterator iterator = bitSet.getIntIterator();
            iterator.advanceIfNeeded(srcPos);
            final int difference = destPos - srcPos;
            try {
                while (true) {
                    final int index = iterator.next();
                    final int cur = index + difference;
                    if (cur > destPos) {
                        break;
                    } else {
                        cValues.bitSet.add(cur);
                    }
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

}
