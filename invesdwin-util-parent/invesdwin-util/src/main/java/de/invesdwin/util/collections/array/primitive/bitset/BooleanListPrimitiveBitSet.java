package de.invesdwin.util.collections.array.primitive.bitset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

@NotThreadSafe
public class BooleanListPrimitiveBitSet implements IPrimitiveBitSet {

    private final BooleanList bitSet;
    private final int size;
    private int trueCount;

    public BooleanListPrimitiveBitSet(final int size) {
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
        if (!bitSet.set(index, true)) {
            trueCount++;
        }
    }

    @Override
    public void remove(final int index) {
        if (bitSet.size() > index) {
            if (bitSet.set(index, false)) {
                trueCount--;
            }
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
    public void flip(final int index) {
        if (bitSet.size() <= index) {
            add(index);
        } else {
            if (bitSet.set(index, !bitSet.getBoolean(index))) {
                trueCount--;
            } else {
                trueCount++;
            }
        }
    }

    @Override
    public void flip(final int index, final int length) {
        for (int i = index; i < index + length; i++) {
            flip(i);
        }
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
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
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
    public int size() {
        return size;
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
    public BooleanListPrimitiveBitSet unwrap() {
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
        bitSet.clear();
        trueCount = 0;
    }

    @Override
    public void clear(final int index, final int length) {
        final int endIndex = index + length;
        for (int i = index; i < endIndex; i++) {
            remove(i);
        }
    }

}
