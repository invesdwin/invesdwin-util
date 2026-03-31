package de.invesdwin.util.collections.array.large.bitset.roaring;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.collections.array.large.bitset.EmptyLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.RoaringBitmaps;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class RoaringLargeBitSet implements ILargeBitSet {
    /**
     * 4294967296L
     */
    public static final long MAX_SIZE = Integer.toUnsignedLong(0xFFFFFFFF) + 1;
    private static final long UNINITIALIZED_TRUE_COUNT = -1;

    private final RoaringBitmap bitSet;
    private final long size;
    private long trueCount;

    public RoaringLargeBitSet(final RoaringBitmap bitSet, final long size) {
        assertSize(size);
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    public RoaringLargeBitSet(final long expectedSize) {
        assertSize(expectedSize);
        this.bitSet = new RoaringBitmap();
        this.size = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    private void assertSize(final long size) {
        if (size < 0 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Size must be between 0 and " + MAX_SIZE);
        }
    }

    @Override
    public int getId() {
        return System.identityHashCode(bitSet);
    }

    @Override
    public void add(final long index) {
        final int i = ByteBuffers.checkedCastUnsigned(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.add(i);
        } else {
            if (bitSet.checkedAdd(i)) {
                trueCount++;
            }
        }
    }

    @Override
    public void remove(final long index) {
        final int i = ByteBuffers.checkedCastUnsigned(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.remove(i);
        } else {
            if (bitSet.checkedRemove(i)) {
                trueCount--;
            }
        }
    }

    @Override
    public boolean contains(final long index) {
        final int i = ByteBuffers.checkedCastUnsigned(index);
        return bitSet.contains(i);
    }

    @Override
    public void flip(final long index) {
        final int i = ByteBuffers.checkedCastUnsigned(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.flip(i);
        } else if (bitSet.contains(i)) {
            bitSet.remove(i);
            trueCount--;
        } else {
            bitSet.add(i);
            trueCount++;
        }
    }

    @Override
    public void flip(final long index, final long length) {
        RoaringBitmaps.flip(bitSet, index, index + length);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    @Override
    public ILargeBitSet optimize() {
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        } else {
            bitSet.trim();
            bitSet.runOptimize();
            return this;
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andUnwrapped(unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
        }
        //merge remaining
        final RoaringLargeBitSet wrapped = new RoaringLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                continue;
            }
            BitSets.and(wrapped, other);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private RoaringBitmap andUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                final RoaringLargeBitSet cUnwrapped = (RoaringLargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.and(cUnwrappeds);
        return combined;
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (fromInclusive == 0 && toExclusive >= bitSet.last()) {
            return and(others);
        }
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        final int unwrappedCount = countUnwrapped(others);
        final RoaringBitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            RoaringBitmaps.remove(combined, 0, fromInclusive);
            RoaringBitmaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final RoaringLargeBitSet wrapped = new RoaringLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                continue;
            }
            BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private RoaringBitmap andRangeUnwrapped(final long fromInclusive, final long toExclusive, final int unwrappedCount,
            final ILargeBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                final RoaringLargeBitSet cUnwrapped = (RoaringLargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.andRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
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
        final RoaringLargeBitSet wrapped = new RoaringLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                continue;
            }
            BitSets.or(wrapped, other);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private RoaringBitmap orUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                final RoaringLargeBitSet cUnwrapped = (RoaringLargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.or(cUnwrappeds);
        return combined;
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
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
        final RoaringLargeBitSet wrapped = new RoaringLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                continue;
            }
            BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private RoaringBitmap orRangeUnwrapped(final long fromInclusive, final long toExclusive, final int unwrappedCount,
            final ILargeBitSet... others) {
        final RoaringBitmap combined;
        final RoaringBitmap[] cUnwrappeds = new RoaringBitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                final RoaringLargeBitSet cUnwrapped = (RoaringLargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = RoaringBitmaps.orRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    private int countUnwrapped(final ILargeBitSet... others) {
        int unwrappedCount = 0;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof RoaringLargeBitSet) {
                unwrappedCount++;
            }
        }
        return unwrappedCount;
    }

    @Override
    public RoaringLargeBitSet negate() {
        final RoaringBitmap negated = bitSet.clone();
        negated.flip(0L, size);
        return new RoaringLargeBitSet(negated, size);
    }

    @Override
    public ILargeBitSet negateShallow() {
        return new ShallowNegatedLargeBitSet(this);
    }

    @Override
    public long getTrueCount() {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            trueCount = bitSet.getLongCardinality();
        }
        return trueCount;
    }

    @Override
    public long size() {
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
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final PeekableIntIterator delegate = bitSet.getIntIterator();
        return new ISkippingLargeIndexProvider() {

            private long cur = -1;

            @Override
            public long next(final long nextCandidate) {
                if (cur == nextCandidate) {
                    return nextCandidate;
                }
                delegate.advanceIfNeeded(ByteBuffers.checkedCastUnsigned(nextCandidate));
                do {
                    if (!delegate.hasNext()) {
                        return ISkippingLargeIndexProvider.END;
                    }
                    cur = Integer.toUnsignedLong(delegate.next());
                } while (cur < nextCandidate);
                return cur;
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        if (length == 0) {
            return;
        }

        if (dest instanceof RoaringLargeBitSet && srcPos == destPos && srcPos == 0 && length >= size) {
            // Fast path
            final RoaringLargeBitSet javaDest = (RoaringLargeBitSet) dest;
            javaDest.clear(destPos, length);
            javaDest.bitSet.or(bitSet);
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public RoaringLargeBitSet unwrap() {
        return this;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

    @Override
    public void clear(final long index, final long length) {
        final long toIndexExclusive = index + length;
        RoaringBitmaps.remove(bitSet, index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
