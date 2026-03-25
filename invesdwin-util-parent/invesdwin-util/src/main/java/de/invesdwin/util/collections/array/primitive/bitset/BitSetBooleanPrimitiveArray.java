package de.invesdwin.util.collections.array.primitive.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimtiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateBooleanPrimitiveArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BitSetBooleanPrimitiveArray implements IBooleanPrimtiveArray {

    private final IPrimitiveBitSet bitSet;

    public BitSetBooleanPrimitiveArray(final int size) {
        this.bitSet = ILockCollectionFactory.getInstance(false).newBitSet(size);
    }

    public BitSetBooleanPrimitiveArray(final IPrimitiveBitSet values) {
        this.bitSet = values;
    }

    @Override
    public int getId() {
        return bitSet.getId();
    }

    @Override
    public void set(final int index, final boolean value) {
        if (value) {
            bitSet.add(index);
        } else {
            bitSet.remove(index);
        }
    }

    @Override
    public boolean get(final int index) {
        return bitSet.contains(index);
    }

    @Override
    public int size() {
        return bitSet.size();
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public IBooleanPrimtiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return asArrayCopy();
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy() {
        return Booleans.checkedCastVector(bitSet);
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        final boolean[] vector = new boolean[length];
        final int limit = fromIndex + length;
        for (int i = fromIndex; i < limit; i++) {
            vector[i] = bitSet.contains(i);
        }
        return vector;
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanPrimtiveArray dest, final int destPos, final int length) {
        final BitSetBooleanPrimitiveArray cDest = ((BitSetBooleanPrimitiveArray) dest);
        bitSet.getBooleans(srcPos, cDest.bitSet, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    public IPrimitiveBitSet getBitSet() {
        return bitSet;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        return bitSet.getBuffer(buffer);
    }

    @Override
    public int getBufferLength() {
        return bitSet.getBufferLength();
    }

    @Override
    public void clear() {
        bitSet.clear();
    }

}
