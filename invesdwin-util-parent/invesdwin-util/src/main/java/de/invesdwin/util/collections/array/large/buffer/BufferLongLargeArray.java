package de.invesdwin.util.collections.array.large.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateLongLargeArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BufferLongLargeArray implements ILongLargeArray {

    private final IMemoryBuffer buffer;

    public BufferLongLargeArray(final IMemoryBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final long index, final long value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        buffer.putLong(index * Long.BYTES, value);
    }

    @Override
    public long get(final long index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return buffer.getLong(index * Long.BYTES);
    }

    @Override
    public long size() {
        return buffer.capacity() / Long.BYTES;
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public ILongLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateLongLargeArray(this, fromIndex, length);
    }

    @Override
    public long[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public long[] asArrayCopy(final long fromIndex, final int length) {
        final long[] array = new long[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getLongs(final long srcPos, final ILongLargeArray dest, final long destPos, final long length) {
        if (dest instanceof BufferLongLargeArray) {
            final BufferLongLargeArray cDest = ((BufferLongLargeArray) dest);
            buffer.getBytes(srcPos * Long.BYTES, cDest.buffer, destPos * Long.BYTES, length * Long.BYTES);
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
