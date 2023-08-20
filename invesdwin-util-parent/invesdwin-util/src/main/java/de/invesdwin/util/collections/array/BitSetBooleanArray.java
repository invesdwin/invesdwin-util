package de.invesdwin.util.collections.array;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BitSetBooleanArray implements IBooleanArray {

    private final IBitSet bitSet;

    public BitSetBooleanArray(final int size) {
        this.bitSet = ILockCollectionFactory.getInstance(false).newBitSet(size);
    }

    public BitSetBooleanArray(final IBitSet values) {
        this.bitSet = values;
    }

    @Override
    public void set(final int index, final boolean value) {
        bitSet.add(index);
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
    public IBooleanArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanArray(this, fromIndex, length);
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
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
        final BitSetBooleanArray cDest = ((BitSetBooleanArray) dest);
        bitSet.getBooleans(srcPos, cDest.bitSet, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    public IBitSet getBitSet() {
        return bitSet;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        return bitSet.getBuffer(buffer);
    }

}
