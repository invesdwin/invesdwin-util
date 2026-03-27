package de.invesdwin.util.collections.array.large.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateIntegerLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class HeapIntegerLargeArray implements IIntegerLargeArray {

    private final int[] values;

    public HeapIntegerLargeArray(final int size) {
        this.values = new int[size];
    }

    public HeapIntegerLargeArray(final int[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final long index, final int value) {
        values[ByteBuffers.checkedCast(index)] = value;
    }

    @Override
    public int get(final long index) {
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
    public IIntegerLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateIntegerLargeArray(this, fromIndex, length);
    }

    public int[] asArray() {
        return values;
    }

    @Override
    public int[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    public int[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public int[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    @Override
    public void getIntegers(final long srcPos, final IIntegerLargeArray dest, final long destPos, final long length) {
        if (dest instanceof HeapIntegerLargeArray) {
            final HeapIntegerLargeArray cDest = ((HeapIntegerLargeArray) dest);
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
            buffer.putDouble(i * Integer.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Integer.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0);
    }

}
