package de.invesdwin.util.collections.array.primitive.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateLongPrimitiveArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferLongPrimitiveArray implements ILongPrimitiveArray {

    private final IByteBuffer buffer;

    public BufferLongPrimitiveArray(final IByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final int index, final long value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        buffer.putLong(index * Long.BYTES, value);
    }

    @Override
    public long get(final int index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return buffer.getLong(index * Long.BYTES);
    }

    @Override
    public int size() {
        return buffer.capacity() / Long.BYTES;
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public ILongPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateLongPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public long[] asArray() {
        return asArrayCopy();
    }

    @Override
    public long[] asArray(final int fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public long[] asArrayCopy() {
        return asArrayCopy(0, size());
    }

    @Override
    public long[] asArrayCopy(final int fromIndex, final int length) {
        final long[] array = new long[length];
        int j = 0;
        for (int i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getLongs(final int srcPos, final ILongPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof BufferLongPrimitiveArray) {
            final BufferLongPrimitiveArray cDest = ((BufferLongPrimitiveArray) dest);
            buffer.getBytes(srcPos * Long.BYTES, cDest.buffer, destPos * Long.BYTES, length * Long.BYTES);
        } else {
            for (int i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer dst) throws IOException {
        buffer.getBytes(0, dst, 0, buffer.capacity());
        return buffer.capacity();
    }

    @Override
    public int getBufferLength() {
        return buffer.capacity();
    }

    @Override
    public void clear() {
        buffer.clear();
    }

}
