package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.ByteBuffers;

@NotThreadSafe
public class HeapDoubleArray implements IDoubleArray {

    private final double[] values;

    public HeapDoubleArray(final int size) {
        this.values = new double[size];
    }

    public HeapDoubleArray(final double[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final double value) {
        values[index] = value;
    }

    @Override
    public double get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IDoubleArray slice(final int fromIndex, final int length) {
        return new SliceDelegateDoubleArray(this, fromIndex, length);
    }

    @Override
    public double[] asArray() {
        return values;
    }

    @Override
    public double[] asArray(final int fromIndex, final int length) {
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
