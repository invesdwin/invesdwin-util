package de.invesdwin.util.collections.array.large.bitset.roaring;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.longlong.PeekableLongIterator;
import org.roaringbitmap.longlong.Roaring64Bitmap;

import de.invesdwin.util.collections.array.large.bitset.EmptyLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Roaring64Bitmaps;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class Roaring64LargeBitSet implements ILargeBitSet {

    private static final long UNINITIALIZED_TRUE_COUNT = -1;
    private final Roaring64Bitmap bitSet;
    private final long size;
    private long trueCount;

    public Roaring64LargeBitSet(final Roaring64Bitmap bitSet, final long size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    public Roaring64LargeBitSet(final long expectedSize) {
        this.bitSet = new Roaring64Bitmap();
        this.size = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    @Override
    public int getId() {
        return System.identityHashCode(bitSet);
    }

    @Override
    public void add(final long index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.add(index);
        } else {
            if (!bitSet.contains(index)) {
                bitSet.add(index);
                trueCount++;
            }
        }
    }

    @Override
    public void remove(final long index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.remove(index);
        } else if (bitSet.contains(index)) {
            bitSet.remove(index);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final long index) {
        return bitSet.contains(index);
    }

    @Override
    public void flip(final long index) {
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
    public void flip(final long index, final long length) {
        bitSet.flip(index, index + length);
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
        final Roaring64Bitmap combined;
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
        final Roaring64LargeBitSet wrapped = new Roaring64LargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                continue;
            }
            BitSets.and(wrapped, other);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private Roaring64Bitmap andUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64Bitmap combined;
        final Roaring64Bitmap[] cUnwrappeds = new Roaring64Bitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                final Roaring64LargeBitSet cUnwrapped = (Roaring64LargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64Bitmaps.and(cUnwrappeds);
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
        final Roaring64Bitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            Roaring64Bitmaps.remove(combined, 0, fromInclusive);
            Roaring64Bitmaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final Roaring64LargeBitSet wrapped = new Roaring64LargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                continue;
            }
            BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private Roaring64Bitmap andRangeUnwrapped(final long fromInclusive, final long toExclusive,
            final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64Bitmap combined;
        final Roaring64Bitmap[] cUnwrappeds = new Roaring64Bitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                final Roaring64LargeBitSet cUnwrapped = (Roaring64LargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64Bitmaps.andRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final int unwrappedCount = countUnwrapped(others);
        final Roaring64Bitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orUnwrapped(unwrappedCount, others);
        } else {
            combined = bitSet.clone();
        }
        //merge remaining
        final Roaring64LargeBitSet wrapped = new Roaring64LargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                continue;
            }
            BitSets.or(wrapped, other);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private Roaring64Bitmap orUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64Bitmap combined;
        final Roaring64Bitmap[] cUnwrappeds = new Roaring64Bitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                final Roaring64LargeBitSet cUnwrapped = (Roaring64LargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64Bitmaps.or(cUnwrappeds);
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
        final Roaring64Bitmap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            Roaring64Bitmaps.remove(combined, 0, fromInclusive);
            Roaring64Bitmaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final Roaring64LargeBitSet wrapped = new Roaring64LargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                continue;
            }
            BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private Roaring64Bitmap orRangeUnwrapped(final long fromInclusive, final long toExclusive, final int unwrappedCount,
            final ILargeBitSet... others) {
        final Roaring64Bitmap combined;
        final Roaring64Bitmap[] cUnwrappeds = new Roaring64Bitmap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeBitSet) {
                final Roaring64LargeBitSet cUnwrapped = (Roaring64LargeBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64Bitmaps.orRange(cUnwrappeds, fromInclusive, toExclusive);
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
            if (unwrapped instanceof Roaring64LargeBitSet) {
                unwrappedCount++;
            }
        }
        return unwrappedCount;
    }

    @Override
    public ILargeBitSet negate() {
        final Roaring64Bitmap negated = bitSet.clone();
        negated.flip(0L, size);
        return new Roaring64LargeBitSet(negated, size);
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

    public Roaring64Bitmap getBitSet() {
        return bitSet;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final PeekableLongIterator delegate = bitSet.getLongIterator();
        return new ISkippingLargeIndexProvider() {

            private long cur = -1;

            @Override
            public long next(final long nextCandidate) {
                if (cur == nextCandidate) {
                    return nextCandidate;
                }
                delegate.advanceIfNeeded(nextCandidate);
                do {
                    if (!delegate.hasNext()) {
                        return ISkippingLargeIndexProvider.END;
                    }
                    cur = delegate.next();
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

        if (dest instanceof Roaring64LargeNavigableBitSet && srcPos == destPos && srcPos == 0 && length >= size) {
            // Fast path
            final Roaring64LargeBitSet javaDest = (Roaring64LargeBitSet) dest;
            javaDest.clear(destPos, length);
            javaDest.bitSet.or(bitSet);
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public Roaring64LargeBitSet unwrap() {
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
        Roaring64Bitmaps.remove(bitSet, index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
