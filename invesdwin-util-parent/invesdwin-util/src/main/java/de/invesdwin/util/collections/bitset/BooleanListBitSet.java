package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

@NotThreadSafe
public class BooleanListBitSet implements IBitSet {

    private final BooleanList bitSet;
    private final int size;
    private int trueCount = 0;

    public BooleanListBitSet(final int size) {
        this.bitSet = new BooleanArrayList(size);
        this.size = size;
    }

    @Override
    public int getId() {
        return System.identityHashCode(this);
    }

    @Override
    public void add(final int index) {
        while (bitSet.size() <= index) {
            bitSet.add(false);
        }
        bitSet.set(index, true);
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        if (bitSet.size() < index) {
            bitSet.set(index, false);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final int index) {
        if (bitSet.size() <= index) {
            return false;
        } else {
            return bitSet.getBoolean(index);
        }
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
    public int size() {
        return size;
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
