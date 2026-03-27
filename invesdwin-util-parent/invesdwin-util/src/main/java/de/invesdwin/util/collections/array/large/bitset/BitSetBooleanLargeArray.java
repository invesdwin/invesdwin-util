package de.invesdwin.util.collections.array.large.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateBooleanLargeArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BitSetBooleanLargeArray implements IBooleanLargeArray {

    private final ILargeBitSet bitSet;

    public BitSetBooleanLargeArray(final long size) {
        this.bitSet = ILockCollectionFactory.getInstance(false).newLargeBitSet(size);
    }

    public BitSetBooleanLargeArray(final ILargeBitSet values) {
        this.bitSet = values;
    }

    @Override
    public int getId() {
        return bitSet.getId();
    }

    @Override
    public void set(final long index, final boolean value) {
        if (value) {
            bitSet.add(index);
        } else {
            bitSet.remove(index);
        }
    }

    @Override
    public boolean get(final long index) {
        return bitSet.contains(index);
    }

    @Override
    public long size() {
        return bitSet.size();
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateBooleanLargeArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        final boolean[] vector = new boolean[length];
        final long limit = fromIndex + length;
        int j = 0;
        for (long i = fromIndex; i < limit; i++, j++) {
            vector[j] = bitSet.contains(i);
        }
        return vector;
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
        if (dest instanceof BitSetBooleanLargeArray) {
            final BitSetBooleanLargeArray cDest = ((BitSetBooleanLargeArray) dest);
            bitSet.getBooleans(srcPos, cDest.bitSet, destPos, length);
        } else {
            for (long i = 0; i < length; i++) {
                dest.set(destPos + i, bitSet.contains(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    public ILargeBitSet getBitSet() {
        return bitSet;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        return bitSet.getBuffer(buffer);
    }

    @Override
    public long getBufferLength() {
        return bitSet.getBufferLength();
    }

    @Override
    public void clear() {
        bitSet.clear();
    }

}
