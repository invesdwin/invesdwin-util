package de.invesdwin.util.collections.array.large.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateIntegerLargeArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BufferIntegerLargeArray implements IIntegerLargeArray {

    private final IMemoryBuffer buffer;

    public BufferIntegerLargeArray(final IMemoryBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final long index, final int value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        buffer.putInt(index * Integer.BYTES, value);
    }

    @Override
    public int get(final long index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return buffer.getInt(index * Integer.BYTES);
    }

    @Override
    public long size() {
        return buffer.capacity() / Integer.BYTES;
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public IIntegerLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateIntegerLargeArray(this, fromIndex, length);
    }

    @Override
    public int[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public int[] asArrayCopy(final long fromIndex, final int length) {
        final int[] array = new int[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getIntegers(final long srcPos, final IIntegerLargeArray dest, final long destPos, final long length) {
        if (dest instanceof BufferIntegerLargeArray) {
            final BufferIntegerLargeArray cDest = ((BufferIntegerLargeArray) dest);
            buffer.getBytes(srcPos * Integer.BYTES, cDest.buffer, destPos * Integer.BYTES, length * Integer.BYTES);
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
