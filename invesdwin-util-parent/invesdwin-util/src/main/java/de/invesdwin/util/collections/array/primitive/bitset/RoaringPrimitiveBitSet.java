package de.invesdwin.util.collections.array.primitive.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.RoaringBitmaps;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class RoaringPrimitiveBitSet implements IPrimitiveBitSet {

    private static final int UNINITIALIZED_TRUE_COUNT = -1;
    private final RoaringBitmap bitSet;
    private final int size;
    private int trueCount;

    public RoaringPrimitiveBitSet(final RoaringBitmap bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
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
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.add(index);
        } else {
            if (bitSet.checkedAdd(index)) {
                trueCount++;
            }
        }
    }

    @Override
    public void remove(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.remove(index);
        } else {
            if (bitSet.checkedRemove(index)) {
                trueCount--;
            }
        }
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.contains(index);
    }

    @Override
    public void flip(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.flip(index);
        } else if (bitSet.contains(index)) {
            bitSet.remove(index);
            trueCount--;
        } else {
            bitSet.add(index);
            trueCount++;
        }
    }

    @Override
    public void flip(final int index, final int length) {
        RoaringBitmaps.flip(bitSet, index, index + length);
        trueCount = UNINITIALIZED_TRUE_COUNT;
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
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andUnwrapped(unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
        }
        //merge remaining
        final RoaringPrimitiveBitSet wrapped = new RoaringPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                continue;
            }
            BitSets.and(wrapped, other);
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private RoaringBitmap andUnwrapped(final int unwrappedCount, final IPrimitiveBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                final RoaringPrimitiveBitSet cUnwrapped = (RoaringPrimitiveBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.and(cUnwrappeds);
        return combined;
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            RoaringBitmaps.remove(combined, 0, fromInclusive);
            RoaringBitmaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final RoaringPrimitiveBitSet wrapped = new RoaringPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                continue;
            }
            BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private RoaringBitmap andRangeUnwrapped(final int fromInclusive, final int toExclusive, final int unwrappedCount,
            final IPrimitiveBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                final RoaringPrimitiveBitSet cUnwrapped = (RoaringPrimitiveBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.andRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orUnwrapped(unwrappedCount, others);
        } else {
            combined = bitSet.clone();
        }
        //merge remaining
        final RoaringPrimitiveBitSet wrapped = new RoaringPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                continue;
            }
            BitSets.or(wrapped, other);
        }
        if (combined.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
    }

    private RoaringBitmap orUnwrapped(final int unwrappedCount, final IPrimitiveBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                final RoaringPrimitiveBitSet cUnwrapped = (RoaringPrimitiveBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.or(cUnwrappeds);
        return combined;
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        if (others == null || others.length == 0) {
            return this;
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            RoaringBitmaps.remove(combined, 0, fromInclusive);
            RoaringBitmaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final RoaringPrimitiveBitSet wrapped = new RoaringPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                continue;
            }
            BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
        }
        if (combined.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
    }

    private RoaringBitmap orRangeUnwrapped(final int fromInclusive, final int toExclusive, final int unwrappedCount,
            final IPrimitiveBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                final RoaringPrimitiveBitSet cUnwrapped = (RoaringPrimitiveBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.orRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    private int countUnwrapped(final IPrimitiveBitSet... others) {
        int unwrappedCount = 0;
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringPrimitiveBitSet) {
                unwrappedCount++;
            }
        }
        return unwrappedCount;
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
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
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
        if (length == 0) {
            return;
        }

        if (dest instanceof RoaringPrimitiveBitSet && srcPos == destPos && srcPos == 0 && length >= size) {
            // Fast path
            final RoaringPrimitiveBitSet javaDest = (RoaringPrimitiveBitSet) dest;
            javaDest.clear(destPos, length);
            javaDest.bitSet.or(bitSet);
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public RoaringPrimitiveBitSet unwrap() {
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

    @Override
    public void clear(final int index, final int length) {
        final int toIndexExclusive = index + length;
        RoaringBitmaps.remove(bitSet, index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
