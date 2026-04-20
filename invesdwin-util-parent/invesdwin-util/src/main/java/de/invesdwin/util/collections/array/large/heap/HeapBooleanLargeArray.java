package de.invesdwin.util.collections.array.large.heap;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.BitSetBooleanLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateBooleanLargeArray;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class HeapBooleanLargeArray implements IBooleanLargeArray {

    public static final int MAX_SIZE = Integer.MAX_VALUE;

    private final boolean[] values;

    public HeapBooleanLargeArray(final int size) {
        this.values = new boolean[size];
    }

    public HeapBooleanLargeArray(final boolean[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final long index, final boolean value) {
        values[ByteBuffers.checkedCast(index)] = value;
    }

    @Override
    public boolean get(final long index) {
        return values[ByteBuffers.checkedCast(index)];
    }

    @Override
    public long size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateBooleanLargeArray(this, fromIndex, length);
    }

    public boolean[] asArray() {
        return values;
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    public boolean[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
        if (dest instanceof HeapBooleanLargeArray) {
            final HeapBooleanLargeArray cDest = ((HeapBooleanLargeArray) dest);
            System.arraycopy(values, ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
                    ByteBuffers.checkedCast(length));
        } else {
            for (long i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        final BitSetBooleanLargeArray delegate = new BitSetBooleanLargeArray(size());
        for (long i = 0; i < size(); i++) {
            delegate.set(i, get(i));
        }
        return delegate.getBuffer(buffer);
    }

    @Override
    public long getBufferLength() {
        return (BitSets.wordIndex(size() - 1) + 1) * Long.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, false);
    }

}
