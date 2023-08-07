package de.invesdwin.util.collections.array;

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

    private final IBitSet values;

    public BitSetBooleanArray(final int size) {
        this.values = ILockCollectionFactory.getInstance(false).newBitSet(size);
    }

    public BitSetBooleanArray(final IBitSet values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final boolean value) {
        values.add(index);
    }

    @Override
    public boolean get(final int index) {
        return values.contains(index);
    }

    @Override
    public int size() {
        return values.getExpectedSize();
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
        return Booleans.checkedCastVector(values);
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        final boolean[] vector = new boolean[length];
        final int limit = fromIndex + length;
        for (int i = fromIndex; i < limit; i++) {
            vector[i] = values.contains(i);
        }
        return vector;
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
        final BitSetBooleanArray cDest = ((BitSetBooleanArray) dest);
        values.getBooleans(srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    public IBitSet getValues() {
        return values;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer) {
        return values.toBuffer(buffer);
    }

}
