package de.invesdwin.util.collections.array.primitive.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.collections.array.primitive.buffer.BufferBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapLongPrimitiveArray;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class JavaPrimitiveBitSet implements IPrimitiveBitSet {

    private final BitSet bitSet;
    private final int size;
    private int trueCount = 0;

    public JavaPrimitiveBitSet(final BitSet bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = -1;
    }

    public JavaPrimitiveBitSet(final int expectedSize) {
        if (expectedSize < 0) {
            this.bitSet = new BitSet();
        } else {
            this.bitSet = new BitSet(expectedSize);
        }
        this.size = expectedSize;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    @Override
    public int getId() {
        return System.identityHashCode(bitSet);
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
    public IPrimitiveBitSet optimize() {
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final JavaPrimitiveBitSet cOther = (JavaPrimitiveBitSet) other.unwrap();
            combined.and(cOther.bitSet);
        }
        return new JavaPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final JavaPrimitiveBitSet cOther = (JavaPrimitiveBitSet) other.unwrap();
            BitSets.andRangeFast(combined, cOther.bitSet, fromInclusive, toExclusive);
        }
        return new JavaPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final JavaPrimitiveBitSet cOther = (JavaPrimitiveBitSet) other.unwrap();
            combined.or(cOther.bitSet);
        }
        return new JavaPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final JavaPrimitiveBitSet cOther = (JavaPrimitiveBitSet) other.unwrap();
            BitSets.orRangeFast(combined, cOther.bitSet, fromInclusive, toExclusive);
        }
        return new JavaPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet negate() {
        final BitSet negated = (BitSet) bitSet.clone();
        negated.flip(0, size);
        return new JavaPrimitiveBitSet(negated, size);
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return new ShallowNegatedPrimitiveBitSet(this) {
            @Override
            public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
                return nextCandidate -> {
                    final int next = bitSet.nextClearBit(nextCandidate);
                    if (next < 0) {
                        return ISkippingPrimitiveIndexProvider.END;
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
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final int next = bitSet.nextSetBit(nextCandidate);
            if (next < 0) {
                return ISkippingPrimitiveIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        if (srcPos == destPos) {
            dest.andRange(destPos, destPos + length, new IPrimitiveBitSet[] { this });
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
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @SuppressWarnings("restriction")
    @Override
    public int getBuffer(final IByteBuffer buffer) {
        buffer.putInt(BufferBooleanPrimitiveArray.LENGTH_INDEX, size);
        long[] words = (long[]) Reflections.getUnsafe().getObject(bitSet, BitSets.BITSET_WORDS_OFFSET);
        final int wordsInUse = (int) Reflections.getUnsafe().getObject(bitSet, BitSets.BITSET_WORDS_IN_USE_OFFSET);
        if (words.length > wordsInUse) {
            words = Arrays.copyOfRange(words, 0, wordsInUse);
        }
        final HeapLongPrimitiveArray delegate = new HeapLongPrimitiveArray(words);
        return BufferBooleanPrimitiveArray.ARRAY_INDEX + delegate.getBuffer(buffer.sliceFrom(BufferBooleanPrimitiveArray.ARRAY_INDEX));
    }

    @SuppressWarnings("restriction")
    @Override
    public int getBufferLength() {
        final int wordsInUse = (int) Reflections.getUnsafe().getObject(bitSet, BitSets.BITSET_WORDS_IN_USE_OFFSET);
        return BufferBooleanPrimitiveArray.ARRAY_INDEX + Long.BYTES * wordsInUse;
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

}
