package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class HeapIntegerArray implements IIntegerArray {

    private final int[] values;

    public HeapIntegerArray(final int size) {
        this.values = new int[size];
    }

    public HeapIntegerArray(final int[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final int value) {
        values[index] = value;
    }

    @Override
    public int get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IIntegerArray slice(final int fromIndex, final int length) {
        return new SliceDelegateIntegerArray(this, fromIndex, length);
    }

    @Override
    public int[] asArray() {
        return values;
    }

    @Override
    public int[] asArray(final int fromIndex, final int length) {
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
