package de.invesdwin.util.collections.array.large.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateDoubleLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class HeapDoubleLargeArray implements IDoubleLargeArray {

    private final double[] values;

    public HeapDoubleLargeArray(final int size) {
        this.values = new double[size];
    }

    public HeapDoubleLargeArray(final double[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final long index, final double value) {
        values[ByteBuffers.checkedCast(index)] = value;
    }

    @Override
    public double get(final long index) {
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
    public IDoubleLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateDoubleLargeArray(this, fromIndex, length);
    }

    public double[] asArray() {
        return values;
    }

    @Override
    public double[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    public double[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public double[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    @Override
    public void getDoubles(final long srcPos, final IDoubleLargeArray dest, final long destPos, final long length) {
        if (dest instanceof HeapDoubleLargeArray) {
            final HeapDoubleLargeArray cDest = ((HeapDoubleLargeArray) dest);
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
        for (long i = 0; i < size(); i++) {
            buffer.putDouble(i * Double.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Double.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0D);
    }

}
