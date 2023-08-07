package de.invesdwin.util.collections.array.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.SliceDelegateDoubleArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

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
    public double[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public double[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getDoubles(final int srcPos, final IDoubleArray dest, final int destPos, final int length) {
        final HeapDoubleArray cDest = ((HeapDoubleArray) dest);
        System.arraycopy(values, srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putDouble(i * Double.BYTES, get(i));
        }
        return size() * Double.BYTES;
    }

}
