package de.invesdwin.util.collections.array.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.SliceDelegateLongArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapLongArray implements ILongArray {

    private final long[] values;

    public HeapLongArray(final int size) {
        this.values = new long[size];
    }

    public HeapLongArray(final long[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final int index, final long value) {
        values[index] = value;
    }

    @Override
    public long get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public ILongArray slice(final int fromIndex, final int length) {
        return new SliceDelegateLongArray(this, fromIndex, length);
    }

    @Override
    public long[] asArray() {
        return values;
    }

    @Override
    public long[] asArray(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public long[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public long[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getLongs(final int srcPos, final ILongArray dest, final int destPos, final int length) {
        final HeapLongArray cDest = ((HeapLongArray) dest);
        System.arraycopy(values, srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putDouble(i * Long.BYTES, get(i));
        }
        return size() * Long.BYTES;
    }

}
