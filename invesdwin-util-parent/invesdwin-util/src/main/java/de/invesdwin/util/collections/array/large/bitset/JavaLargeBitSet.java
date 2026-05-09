package de.invesdwin.util.collections.array.large.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.UnsafeApi;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.collections.array.large.buffer.BufferBooleanLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapLongLargeArray;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class JavaLargeBitSet implements ILargeBitSet {

    private static final int UNINITIALIZED_TRUE_COUNT = -1;
    private final BitSet bitSet;
    private final int size;
    private int trueCount;

    public JavaLargeBitSet(final BitSet bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    public JavaLargeBitSet(final int expectedSize) {
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
    public void add(final long index) {
        final int i = ByteBuffers.checkedCast(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.set(i);
        } else {
            if (!bitSet.get(i)) {
                bitSet.set(i);
                trueCount++;
            }
        }
    }

    @Override
    public void remove(final long index) {
        final int i = ByteBuffers.checkedCast(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.clear(i);
        } else if (bitSet.get(i)) {
            bitSet.clear(i);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final long index) {
        return bitSet.get(ByteBuffers.checkedCast(index));
    }

    @Override
    public void flip(final long index) {
        final int i = ByteBuffers.checkedCast(index);
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.flip(i);
        } else if (bitSet.get(i)) {
            bitSet.clear(i);
            trueCount--;
        } else {
            bitSet.set(i);
            trueCount++;
        }
    }

    @Override
    public void flip(final long index, final long length) {
        bitSet.flip(ByteBuffers.checkedCast(index), ByteBuffers.checkedCast(index + length));
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    @Override
    public ILargeBitSet optimize() {
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        } else {
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
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaLargeBitSet wrapped = new JavaLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaLargeBitSet) {
                final JavaLargeBitSet cUnwrapped = (JavaLargeBitSet) unwrapped;
                BitSets.and(combined, cUnwrapped.bitSet);
            } else {
                BitSets.and(wrapped, other);
            }
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
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
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaLargeBitSet wrapped = new JavaLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaLargeBitSet) {
                final JavaLargeBitSet cUnwrapped = (JavaLargeBitSet) unwrapped;
                BitSets.andRange(combined, cUnwrapped.bitSet, ByteBuffers.checkedCast(fromInclusive),
                        ByteBuffers.checkedCast(toExclusive));
            } else {
                BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
            }
            if (combined.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaLargeBitSet wrapped = new JavaLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaLargeBitSet) {
                final JavaLargeBitSet cUnwrapped = (JavaLargeBitSet) unwrapped;
                BitSets.or(combined, cUnwrapped.bitSet);
            } else {
                BitSets.or(wrapped, other);
            }
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final BitSet combined = (BitSet) bitSet.clone();
        final JavaLargeBitSet wrapped = new JavaLargeBitSet(combined, size);
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                continue;
            }
            final ILargeBitSet unwrapped = other.unwrap();
            if (unwrapped instanceof JavaLargeBitSet) {
                final JavaLargeBitSet cUnwrapped = (JavaLargeBitSet) unwrapped;
                BitSets.orRange(combined, cUnwrapped.bitSet, ByteBuffers.checkedCast(fromInclusive),
                        ByteBuffers.checkedCast(toExclusive));
            } else {
                BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
            }
        }
        if (combined.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet negate() {
        final BitSet negated = (BitSet) bitSet.clone();
        negated.flip(0, size);
        return new JavaLargeBitSet(negated, size);
    }

    @Override
    public ILargeBitSet negateShallow() {
        return new ShallowNegatedLargeBitSet(this) {
            @Override
            public ISkippingLargeIndexProvider newSkippingIndexProvider() {
                return nextCandidate -> {
                    final int next = bitSet.nextClearBit(ByteBuffers.checkedCast(nextCandidate));
                    if (next < 0) {
                        return ISkippingLargeIndexProvider.END;
                    } else {
                        return next;
                    }
                };
            }
        };
    }

    @Override
    public long getTrueCount() {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            trueCount = bitSet.cardinality();
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

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final int next = bitSet.nextSetBit(ByteBuffers.checkedCast(nextCandidate));
            if (next < 0) {
                return ISkippingLargeIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        if (length == 0) {
            return;
        }

        if (dest instanceof JavaLargeBitSet && srcPos == destPos) {
            // Fast path
            final JavaLargeBitSet javaDest = (JavaLargeBitSet) dest;
            javaDest.clear(destPos, length);
            BitSets.orRange(javaDest.bitSet, bitSet, ByteBuffers.checkedCast(destPos),
                    ByteBuffers.checkedCast(destPos + length));
        } else {
            // Generic path for other implementations
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public ILargeBitSet unwrap() {
        return this;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        buffer.putLong(BufferBooleanLargeArray.LENGTH_INDEX, size);
        long[] words = (long[]) UnsafeApi.getReference(bitSet, BitSets.BITSET_WORDS_OFFSET);
        final int wordsInUse = (int) UnsafeApi.getReference(bitSet, BitSets.BITSET_WORDS_IN_USE_OFFSET);
        if (words.length > wordsInUse) {
            words = Arrays.copyOfRange(words, 0, wordsInUse);
        }
        final HeapLongLargeArray delegate = new HeapLongLargeArray(words);
        return BufferBooleanLargeArray.ARRAY_INDEX
                + delegate.getBuffer(buffer.sliceFrom(BufferBooleanLargeArray.ARRAY_INDEX));
    }

    @Override
    public long getBufferLength() {
        final int wordsInUse = (int) UnsafeApi.getReference(bitSet, BitSets.BITSET_WORDS_IN_USE_OFFSET);
        return BufferBooleanLargeArray.ARRAY_INDEX + Long.BYTES * wordsInUse;
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

    @Override
    public void clear(final long index, final long length) {
        final int endIndexExclusive = ByteBuffers.checkedCast(index + length);
        bitSet.clear(ByteBuffers.checkedCast(index), endIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
