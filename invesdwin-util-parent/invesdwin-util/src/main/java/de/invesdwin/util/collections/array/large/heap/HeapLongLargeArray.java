package de.invesdwin.util.collections.array.large.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateLongLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class HeapLongLargeArray implements ILongLargeArray {

    public static final int MAX_SIZE = Integer.MAX_VALUE;

    private final long[] values;

    public HeapLongLargeArray(final int size) {
        this.values = new long[size];
    }

    public HeapLongLargeArray(final long[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final long index, final long value) {
        values[ByteBuffers.checkedCast(index)] = value;
    }

    @Override
    public long get(final long index) {
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
    public ILongLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateLongLargeArray(this, fromIndex, length);
    }

    public long[] asArray() {
        return values;
    }

    @Override
    public long[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    public long[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public long[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    @Override
    public void getLongs(final long srcPos, final ILongLargeArray dest, final long destPos, final long length) {
        if (dest instanceof HeapLongLargeArray) {
            final HeapLongLargeArray cDest = ((HeapLongLargeArray) dest);
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
    public long getBuffer(final IMemoryBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putLong(i, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Long.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0L);
    }

}
