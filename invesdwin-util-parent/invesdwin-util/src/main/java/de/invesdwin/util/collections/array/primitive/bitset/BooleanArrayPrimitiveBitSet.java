package de.invesdwin.util.collections.array.primitive.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BooleanArrayPrimitiveBitSet implements IPrimitiveBitSet {

    private final boolean[] bitSet;
    private int trueCount = 0;

    public BooleanArrayPrimitiveBitSet(final int size) {
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
    public IPrimitiveBitSet optimize() {
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return new ShallowNegatedPrimitiveBitSet(this);
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
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return null;
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        Arrays.fill(bitSet, false);
        trueCount = 0;
    }

}
