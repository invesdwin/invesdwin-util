package de.invesdwin.util.collections.array.primitive.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.collections.array.primitive.buffer.BufferBooleanPrimitiveArray;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class LongArrayPrimitiveBitSet implements IPrimitiveBitSet {

    private final LongArrayPrimitiveBitSetBase bitSet;
    private final int size;
    private int trueCount = 0;

    public LongArrayPrimitiveBitSet(final LongArrayPrimitiveBitSetBase bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = -1;
    }

    public LongArrayPrimitiveBitSet(final int size) {
        this.bitSet = new LongArrayPrimitiveBitSetBase(size);
        this.size = size;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    @Override
    public int getId() {
        return bitSet.getId();
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
        final LongArrayPrimitiveBitSetBase combined = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final LongArrayPrimitiveBitSet cOther = (LongArrayPrimitiveBitSet) other.unwrap();
            combined.and(cOther.bitSet);
        }
        return new LongArrayPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        final LongArrayPrimitiveBitSetBase combined = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
            final LongArrayPrimitiveBitSet cOther = (LongArrayPrimitiveBitSet) other.unwrap();
            combined.andRangeFast(cOther.bitSet, fromInclusive, toExclusive);
        }
        return new LongArrayPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        final LongArrayPrimitiveBitSetBase combined = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final LongArrayPrimitiveBitSet cOther = (LongArrayPrimitiveBitSet) other.unwrap();
            combined.or(cOther.bitSet);
        }
        return new LongArrayPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        final LongArrayPrimitiveBitSetBase combined = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final LongArrayPrimitiveBitSet cOther = (LongArrayPrimitiveBitSet) other.unwrap();
            combined.orRangeFast(cOther.bitSet, fromInclusive, toExclusive);
        }
        return new LongArrayPrimitiveBitSet(combined, size);
    }

    @Override
    public IPrimitiveBitSet negate() {
        final LongArrayPrimitiveBitSetBase negated = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        negated.flip(0, size);
        return new LongArrayPrimitiveBitSet(negated, size);
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
        final LongArrayPrimitiveBitSet cDest = (LongArrayPrimitiveBitSet) dest.unwrap();
        bitSet.getWords().getLongs(srcPos, cDest.bitSet.getWords(), destPos, length);
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        buffer.putInt(BufferBooleanPrimitiveArray.LENGTH_INDEX, size);
        return BufferBooleanPrimitiveArray.ARRAY_INDEX
                + bitSet.getWords().getBuffer(buffer.sliceFrom(BufferBooleanPrimitiveArray.ARRAY_INDEX));
    }

    @Override
    public int getBufferLength() {
        return BufferBooleanPrimitiveArray.ARRAY_INDEX + bitSet.getWords().getBufferLength();
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

}
