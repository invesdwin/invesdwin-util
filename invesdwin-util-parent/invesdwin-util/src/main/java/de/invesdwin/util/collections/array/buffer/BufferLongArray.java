package de.invesdwin.util.collections.array.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.SliceDelegateLongArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferLongArray implements ILongArray {

    private final IByteBuffer buffer;

    public BufferLongArray(final IByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final int index, final long value) {
        buffer.putLong(index * Long.BYTES, value);
    }

    @Override
    public long get(final int index) {
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
    public ILongArray slice(final int fromIndex, final int length) {
        return new SliceDelegateLongArray(this, fromIndex, length);
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
        for (int i = fromIndex; i < length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    @Override
    public void getLongs(final int srcPos, final ILongArray dest, final int destPos, final int length) {
        final BufferLongArray cDest = ((BufferLongArray) dest);
        buffer.getBytes(srcPos * Long.BYTES, cDest.buffer, destPos * Long.BYTES, length * Long.BYTES);
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

}
