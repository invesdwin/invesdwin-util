package de.invesdwin.util.collections.array.primitive.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateDoublePrimitiveArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapDoublePrimitiveArray implements IDoublePrimitiveArray {

    private final double[] values;

    public HeapDoublePrimitiveArray(final int size) {
        this.values = new double[size];
    }

    public HeapDoublePrimitiveArray(final double[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
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
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IDoublePrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateDoublePrimitiveArray(this, fromIndex, length);
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
    public void getDoubles(final int srcPos, final IDoublePrimitiveArray dest, final int destPos, final int length) {
        final HeapDoublePrimitiveArray cDest = ((HeapDoublePrimitiveArray) dest);
        System.arraycopy(values, srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putDouble(i * Double.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public int getBufferLength() {
        return size() * Double.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0D);
    }

}
