package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

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
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

}
