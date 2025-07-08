package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BooleanArrayBitSet implements IBitSet {

    private final boolean[] bitSet;
    private int trueCount = 0;

    public BooleanArrayBitSet(final int size) {
        this.bitSet = new boolean[size];
    }

    @Override
    public int getId() {
        return System.identityHashCode(this);
    }

    @Override
    public int size() {
        return bitSet.length;
    }

    @Override
    public void add(final int index) {
        bitSet[index] = true;
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        bitSet[index] = false;
        trueCount--;
    }

    @Override
    public boolean contains(final int index) {
        return bitSet[index];
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
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet negateShallow() {
        return new ShallowNegatedBitSet(this);
    }

    @Override
    public int getTrueCount() {
        return trueCount;
    }

    @Override
    public boolean isEmpty() {
        return trueCount == 0;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return null;
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

}
