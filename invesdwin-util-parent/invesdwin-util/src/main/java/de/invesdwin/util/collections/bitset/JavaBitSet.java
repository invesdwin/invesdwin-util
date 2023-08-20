package de.invesdwin.util.collections.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.buffer.BufferBooleanArray;
import de.invesdwin.util.collections.array.heap.HeapLongArray;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class JavaBitSet implements IBitSet {

    private final BitSet bitSet;
    private final int size;
    private int trueCount = 0;

    public JavaBitSet(final BitSet bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = -1;
    }

    public JavaBitSet(final int expectedSize) {
        if (expectedSize < 0) {
            this.bitSet = new BitSet();
        } else {
            this.bitSet = new BitSet(expectedSize);
        }
        this.size = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    @Override
    public void add(final int index) {
        bitSet.set(index);
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        bitSet.clear(index);
        trueCount = -1;
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.get(index);
    }

    @Override
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            combined.and(cOther.bitSet);
        }
        return new JavaBitSet(combined, size);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            BitSets.andRangeFast(combined, cOther.bitSet, fromInclusive, toExclusive);
        }
        return new JavaBitSet(combined, size);
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            combined.or(cOther.bitSet);
        }
        return new JavaBitSet(combined, size);
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            BitSets.orRangeFast(combined, cOther.bitSet, fromInclusive, toExclusive);
        }
        return new JavaBitSet(combined, size);
    }

    @Override
    public IBitSet negate() {
        final BitSet negated = (BitSet) bitSet.clone();
        negated.flip(0, size);
        return new JavaBitSet(negated, size);
    }

    @Override
    public IBitSet negateShallow() {
        return new ShallowNegatedBitSet(this) {
            @Override
            public ISkippingIndexProvider newSkippingIndexProvider() {
                return nextCandidate -> {
                    final int next = bitSet.nextClearBit(nextCandidate);
                    if (next < 0) {
                        return ISkippingIndexProvider.END;
                    } else {
                        return next;
                    }
                };
            }
        };
    }

    @Override
    public int getTrueCount() {
        if (trueCount == -1) {
            trueCount = bitSet.cardinality();
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

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final int next = bitSet.nextSetBit(nextCandidate);
            if (next < 0) {
                return ISkippingIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        if (srcPos == destPos) {
            dest.andRange(destPos, destPos + length, new IBitSet[] { this });
        } else {
            for (int i = 0; i < length; i++) {
                final boolean contains = contains(srcPos + i);
                if (contains) {
                    dest.add(destPos + i);
                } else {
                    dest.remove(destPos + i);
                }
            }
        }
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

    @SuppressWarnings("restriction")
    @Override
    public int getBuffer(final IByteBuffer buffer) {
        buffer.putInt(BufferBooleanArray.LENGTH_INDEX, size);
        long[] words = (long[]) Reflections.getUnsafe().getObject(bitSet, BitSets.BITSET_WORDS_OFFSET);
        final int wordsInUse = (int) Reflections.getUnsafe().getObject(bitSet, BitSets.BITSET_WORDS_IN_USE_OFFSET);
        if (words.length > wordsInUse) {
            words = Arrays.copyOfRange(words, 0, wordsInUse);
        }
        final HeapLongArray delegate = new HeapLongArray(words);
        return delegate.getBuffer(buffer.sliceFrom(BufferBooleanArray.ARRAY_INDEX));
    }

}
