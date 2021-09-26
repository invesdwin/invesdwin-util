package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class HeapBooleanArray implements IBooleanArray {

    private final boolean[] values;

    public HeapBooleanArray(final int size) {
        this.values = new boolean[size];
    }

    public HeapBooleanArray(final boolean[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final boolean value) {
        values[index] = value;
    }

    @Override
    public boolean get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IBooleanArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return values;
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
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
