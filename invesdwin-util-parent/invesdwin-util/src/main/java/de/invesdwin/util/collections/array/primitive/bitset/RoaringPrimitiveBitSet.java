package de.invesdwin.util.collections.array.primitive.bitset;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class RoaringPrimitiveBitSet implements IPrimitiveBitSet {

    private final RoaringBitmap bitSet;
    private final int size;
    private int trueCount = 0;

    public RoaringPrimitiveBitSet(final RoaringBitmap bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = -1;
    }

    public RoaringPrimitiveBitSet(final int expectedSize) {
        this.bitSet = new RoaringBitmap();
        this.size = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    @Override
    public int getId() {
        return System.identityHashCode(bitSet);
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
    public IPrimitiveBitSet optimize() {
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        } else {
            bitSet.trim();
            bitSet.runOptimize();
            return this;
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final RoaringPrimitiveBitSet cOther = (RoaringPrimitiveBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = FastAggregation.and(cOthers);
        return new RoaringPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final RoaringPrimitiveBitSet cOther = (RoaringPrimitiveBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = RoaringBitmap.and(new ArrayCloseableIterator<RoaringBitmap>(cOthers),
                fromInclusive, (long) toExclusive);
        return new RoaringPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final RoaringPrimitiveBitSet cOther = (RoaringPrimitiveBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = FastAggregation.or(cOthers);
        return new RoaringPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        final RoaringBitmap[] cOthers = new RoaringBitmap[others.length + 1];
        cOthers[0] = bitSet;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final RoaringPrimitiveBitSet cOther = (RoaringPrimitiveBitSet) other.unwrap();
            cOthers[i + 1] = cOther.bitSet;
        }
        final RoaringBitmap combined = RoaringBitmap.or(new ArrayCloseableIterator<RoaringBitmap>(cOthers),
                fromInclusive, (long) toExclusive);
        return new RoaringPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet negate() {
        final RoaringBitmap negated = bitSet.clone();
        negated.flip(0L, size);
        return new RoaringPrimitiveBitSet(negated, size);
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return new ShallowNegatedPrimitiveBitSet(this);
    }

    @Override
    public int getTrueCount() {
        if (trueCount == -1) {
            trueCount = bitSet.getCardinality();
        }
        return trueCount;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    public RoaringBitmap getBitSet() {
        return bitSet;
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        final PeekableIntIterator delegate = bitSet.getIntIterator();
        return new ISkippingPrimitiveIndexProvider() {

            private int cur = -1;

            @Override
            public int next(final int nextCandidate) {
                if (cur == nextCandidate) {
                    return nextCandidate;
                }
                delegate.advanceIfNeeded(nextCandidate);
                do {
                    if (!delegate.hasNext()) {
                        return ISkippingPrimitiveIndexProvider.END;
                    }
                    cur = delegate.next();
                } while (cur < nextCandidate);
                return cur;
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        if (srcPos == destPos) {
            dest.andRange(destPos, destPos + length, new IPrimitiveBitSet[] { this });
        } else {
            final RoaringPrimitiveBitSet cValues = (RoaringPrimitiveBitSet) dest;
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
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

}
