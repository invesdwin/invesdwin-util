package de.invesdwin.util.collections.array.large.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateDoubleLargeArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BufferDoubleLargeArray implements IDoubleLargeArray {

    private final IMemoryBuffer buffer;

    public BufferDoubleLargeArray(final IMemoryBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final long index, final double value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        buffer.putDouble(index * Double.BYTES, value);
    }

    @Override
    public double get(final long index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return buffer.getDouble(index * Double.BYTES);
    }

    @Override
    public long size() {
        return buffer.capacity() / Double.BYTES;
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public IDoubleLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateDoubleLargeArray(this, fromIndex, length);
    }

    @Override
    public double[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public double[] asArrayCopy(final long fromIndex, final int length) {
        final double[] array = new double[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getDoubles(final long srcPos, final IDoubleLargeArray dest, final long destPos, final long length) {
        if (dest instanceof BufferDoubleLargeArray) {
            final BufferDoubleLargeArray cDest = ((BufferDoubleLargeArray) dest);
            buffer.getBytes(srcPos * Double.BYTES, cDest.buffer, destPos * Double.BYTES, length * Double.BYTES);
        } else {
            for (int i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    @Override
    public long getBuffer(final IMemoryBuffer dst) throws IOException {
        buffer.getBytes(0, dst, 0, buffer.capacity());
        return buffer.capacity();
    }

    @Override
    public long getBufferLength() {
        return buffer.capacity();
    }

    @Override
    public void clear() {
        buffer.clear();
    }
}
