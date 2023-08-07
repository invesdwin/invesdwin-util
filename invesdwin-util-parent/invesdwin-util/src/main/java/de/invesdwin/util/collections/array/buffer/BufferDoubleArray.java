package de.invesdwin.util.collections.array.buffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.SliceDelegateDoubleArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferDoubleArray implements IDoubleArray {

    private final IByteBuffer buffer;

    public BufferDoubleArray(final IByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void set(final int index, final double value) {
        buffer.putDouble(index * Double.BYTES, value);
    }

    @Override
    public double get(final int index) {
        return buffer.getDouble(index * Double.BYTES);
    }

    @Override
    public int size() {
        return buffer.capacity() / Double.BYTES;
    }

    @Override
    public IDoubleArray slice(final int fromIndex, final int length) {
        return new SliceDelegateDoubleArray(this, fromIndex, length);
    }

    @Override
    public double[] asArray() {
        return asArrayCopy();
    }

    @Override
    public double[] asArray(final int fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public double[] asArrayCopy() {
        return asArrayCopy(0, size());
    }

    @Override
    public double[] asArrayCopy(final int fromIndex, final int length) {
        final double[] array = new double[length];
        for (int i = fromIndex; i < length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    @Override
    public void getDoubles(final int srcPos, final IDoubleArray dest, final int destPos, final int length) {
        final BufferDoubleArray cDest = ((BufferDoubleArray) dest);
        buffer.getBytes(srcPos * Double.BYTES, cDest.buffer, destPos * Double.BYTES, length * Double.BYTES);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer) {
        this.buffer.getBytes(0, buffer);
        return this.buffer.capacity();
    }

}
