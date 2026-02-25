package de.invesdwin.util.collections.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.buffer.BufferBooleanArray;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class LongArrayBitSet implements IBitSet {

    private final LongArrayBitSetBase bitSet;
    private final int size;
    private int trueCount = 0;

    public LongArrayBitSet(final LongArrayBitSetBase bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = -1;
    }

    public LongArrayBitSet(final int size) {
        this.bitSet = new LongArrayBitSetBase(size);
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
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        final LongArrayBitSetBase combined = new LongArrayBitSetBase(
                ILongArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final LongArrayBitSet cOther = (LongArrayBitSet) other.unwrap();
            combined.and(cOther.bitSet);
        }
        return new LongArrayBitSet(combined, size);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final LongArrayBitSetBase combined = new LongArrayBitSetBase(
                ILongArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final LongArrayBitSet cOther = (LongArrayBitSet) other.unwrap();
            combined.andRangeFast(cOther.bitSet, fromInclusive, toExclusive);
        }
        return new LongArrayBitSet(combined, size);
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        final LongArrayBitSetBase combined = new LongArrayBitSetBase(
                ILongArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final LongArrayBitSet cOther = (LongArrayBitSet) other.unwrap();
            combined.or(cOther.bitSet);
        }
        return new LongArrayBitSet(combined, size);
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final LongArrayBitSetBase combined = new LongArrayBitSetBase(
                ILongArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty() || combined.isEmpty()) {
                continue;
            }
            final LongArrayBitSet cOther = (LongArrayBitSet) other.unwrap();
            combined.orRangeFast(cOther.bitSet, fromInclusive, toExclusive);
        }
        return new LongArrayBitSet(combined, size);
    }

    @Override
    public IBitSet negate() {
        final LongArrayBitSetBase negated = new LongArrayBitSetBase(
                ILongArray.newInstance(bitSet.getWords().asArrayCopy()), size);
        negated.flip(0, size);
        return new LongArrayBitSet(negated, size);
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
        final LongArrayBitSet cDest = (LongArrayBitSet) dest.unwrap();
        bitSet.getWords().getLongs(srcPos, cDest.bitSet.getWords(), destPos, length);
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        buffer.putInt(BufferBooleanArray.LENGTH_INDEX, size);
        return bitSet.getWords().getBuffer(buffer.sliceFrom(BufferBooleanArray.ARRAY_INDEX));
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

}
