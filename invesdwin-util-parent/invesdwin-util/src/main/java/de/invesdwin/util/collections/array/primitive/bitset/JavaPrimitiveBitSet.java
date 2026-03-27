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

    private static final int UNINITIALIZED_TRUE_COUNT = -1;
    private final BitSet bitSet;
    private final int size;
    private int trueCount;

    public JavaPrimitiveBitSet(final BitSet bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
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
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.set(index);
        } else {
            if (!bitSet.get(index)) {
                bitSet.set(index);
                trueCount++;
            }
        }
    }

    @Override
    public void remove(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.clear(index);
        } else {
            if (bitSet.get(index)) {
                bitSet.clear(index);
                trueCount--;
            }
        }
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.get(index);
    }

    @Override
    public void flip(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.flip(index);
        } else {
            if (bitSet.get(index)) {
                bitSet.clear(index);
                trueCount--;
            } else {
                bitSet.set(index);
                trueCount++;
            }
        }
    }

    @Override
    public void flip(final int index, final int length) {
        bitSet.flip(index, index + length);
        trueCount = UNINITIALIZED_TRUE_COUNT;
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
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaPrimitiveBitSet wrapped = new JavaPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaPrimitiveBitSet) {
                final JavaPrimitiveBitSet cUnwrapped = (JavaPrimitiveBitSet) unwrapped;
                BitSets.and(combined, cUnwrapped.bitSet);
            } else {
                BitSets.and(wrapped, other);
            }
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
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
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaPrimitiveBitSet wrapped = new JavaPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaPrimitiveBitSet) {
                final JavaPrimitiveBitSet cUnwrapped = (JavaPrimitiveBitSet) unwrapped;
                BitSets.andRange(combined, cUnwrapped.bitSet, fromInclusive, toExclusive);
            } else {
                BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            }
            if (combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaPrimitiveBitSet wrapped = new JavaPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaPrimitiveBitSet) {
                final JavaPrimitiveBitSet cUnwrapped = (JavaPrimitiveBitSet) unwrapped;
                BitSets.or(combined, cUnwrapped.bitSet);
            } else {
                BitSets.or(wrapped, other);
            }
        }
        if (combined.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaPrimitiveBitSet wrapped = new JavaPrimitiveBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final IPrimitiveBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaPrimitiveBitSet) {
                final JavaPrimitiveBitSet cUnwrapped = (JavaPrimitiveBitSet) unwrapped;
                BitSets.orRange(combined, cUnwrapped.bitSet, fromInclusive, toExclusive);
            } else {
                BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
            }
        }
        if (combined.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
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
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
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
        if (length == 0) {
            return;
        }

        if (dest instanceof JavaPrimitiveBitSet && srcPos == destPos) {
            // Fast path
            final JavaPrimitiveBitSet javaDest = (JavaPrimitiveBitSet) dest;
            javaDest.clear(destPos, length);
            BitSets.orRange(javaDest.bitSet, bitSet, destPos, destPos + length);
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public JavaPrimitiveBitSet unwrap() {
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
        return BufferBooleanPrimitiveArray.ARRAY_INDEX
                + delegate.getBuffer(buffer.sliceFrom(BufferBooleanPrimitiveArray.ARRAY_INDEX));
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

    @Override
    public void clear(final int index, final int length) {
        final int toIndexExclusive = index + length;
        bitSet.clear(index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
