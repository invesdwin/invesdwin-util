package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@NotThreadSafe
public class DirectBufferDoubleArray implements IDoubleArray {

    private final IByteBuffer values;
    private final int size;

    public DirectBufferDoubleArray(final int size) {
        this.values = ByteBuffers.allocateDirect(size * Double.BYTES);
        this.size = size;
    }

    @Override
    public void set(final int index, final double value) {
        values.putDouble(index, value);
    }

    @Override
    public double get(final int index) {
        return values.getDouble(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public IDoubleArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double[] asArray() {
        throw new UnsupportedOperationException();
    }

}
