package de.invesdwin.util.collections.array.large.bitset.roaring;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.longlong.LongIterator;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import de.invesdwin.util.collections.array.large.bitset.EmptyLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Roaring64NavigableMaps;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class Roaring64LargeNavigableBitSet implements ILargeBitSet {

    private static final long UNINITIALIZED_TRUE_COUNT = -1;
    private final Roaring64NavigableMap bitSet;
    private final long size;
    private long trueCount;

    public Roaring64LargeNavigableBitSet(final Roaring64NavigableMap bitSet, final long size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    public Roaring64LargeNavigableBitSet(final long expectedSize) {
        this.bitSet = new Roaring64NavigableMap();
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
            bitSet.removeLong(index);
        } else if (bitSet.contains(index)) {
            bitSet.removeLong(index);
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
            bitSet.removeLong(index);
            trueCount--;
        } else {
            bitSet.add(index);
            trueCount++;
        }
    }

    @Override
    public void flip(final long index, final long length) {
        for (long i = index; i < index + length; i++) {
            bitSet.flip(i);
        }
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
        final Roaring64NavigableMap combined;
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
        final Roaring64LargeNavigableBitSet wrapped = new Roaring64LargeNavigableBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                continue;
            }
            BitSets.and(wrapped, other);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private Roaring64NavigableMap andUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64NavigableMap combined;
        final Roaring64NavigableMap[] cUnwrappeds = new Roaring64NavigableMap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                final Roaring64LargeNavigableBitSet cUnwrapped = (Roaring64LargeNavigableBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64NavigableMaps.and(cUnwrappeds);
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
        final Roaring64NavigableMap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = andRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            Roaring64NavigableMaps.remove(combined, 0, fromInclusive);
            Roaring64NavigableMaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final Roaring64LargeNavigableBitSet wrapped = new Roaring64LargeNavigableBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                continue;
            }
            BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    private Roaring64NavigableMap andRangeUnwrapped(final long fromInclusive, final long toExclusive,
            final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64NavigableMap combined;
        final Roaring64NavigableMap[] cUnwrappeds = new Roaring64NavigableMap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                final Roaring64LargeNavigableBitSet cUnwrapped = (Roaring64LargeNavigableBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64NavigableMaps.andRange(cUnwrappeds, fromInclusive, toExclusive);
        return combined;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final int unwrappedCount = countUnwrapped(others);
        final Roaring64NavigableMap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orUnwrapped(unwrappedCount, others);
        } else {
            combined = bitSet.clone();
        }
        //merge remaining
        final Roaring64LargeNavigableBitSet wrapped = new Roaring64LargeNavigableBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                continue;
            }
            BitSets.or(wrapped, other);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private Roaring64NavigableMap orUnwrapped(final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64NavigableMap combined;
        final Roaring64NavigableMap[] cUnwrappeds = new Roaring64NavigableMap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                final Roaring64LargeNavigableBitSet cUnwrapped = (Roaring64LargeNavigableBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64NavigableMaps.or(cUnwrappeds);
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
        final Roaring64NavigableMap combined;
        if (unwrappedCount > 0) {
            //merge unwrapped
            combined = orRangeUnwrapped(fromInclusive, toExclusive, unwrappedCount, others);
        } else {
            combined = bitSet.clone();
            //remove values outside of range
            Roaring64NavigableMaps.remove(combined, 0, fromInclusive);
            Roaring64NavigableMaps.remove(combined, toExclusive, size);
        }
        //merge remaining
        final Roaring64LargeNavigableBitSet wrapped = new Roaring64LargeNavigableBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                continue;
            }
            BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    private Roaring64NavigableMap orRangeUnwrapped(final long fromInclusive, final long toExclusive,
            final int unwrappedCount, final ILargeBitSet... others) {
        final Roaring64NavigableMap combined;
        final Roaring64NavigableMap[] cUnwrappeds = new Roaring64NavigableMap[unwrappedCount + 1];
        cUnwrappeds[0] = bitSet;
        int nextUnwrappedIndex = 1;
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                final Roaring64LargeNavigableBitSet cUnwrapped = (Roaring64LargeNavigableBitSet) unwrapped;
                cUnwrappeds[nextUnwrappedIndex] = cUnwrapped.bitSet;
                nextUnwrappedIndex++;
            }
        }
        combined = Roaring64NavigableMaps.orRange(cUnwrappeds, fromInclusive, toExclusive);
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
            if (unwrapped instanceof Roaring64LargeNavigableBitSet) {
                unwrappedCount++;
            }
        }
        return unwrappedCount;
    }

    @Override
    public ILargeBitSet negate() {
        final Roaring64NavigableMap negated = bitSet.clone();
        for (long i = 0; i < size; i++) {
            negated.flip(i);
        }
        return new Roaring64LargeNavigableBitSet(negated, size);
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

    public Roaring64NavigableMap getBitSet() {
        return bitSet;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        final LongIterator delegate = bitSet.getLongIterator();
        return new ISkippingLargeIndexProvider() {

            private long cur = -1;

            @Override
            public long next(final long nextCandidate) {
                if (cur == nextCandidate) {
                    return nextCandidate;
                }
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
            final Roaring64LargeNavigableBitSet javaDest = (Roaring64LargeNavigableBitSet) dest;
            javaDest.clear(destPos, length);
            javaDest.bitSet.or(bitSet);
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public Roaring64LargeNavigableBitSet unwrap() {
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
        Roaring64NavigableMaps.remove(bitSet, index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
