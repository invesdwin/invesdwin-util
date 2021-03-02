package de.invesdwin.util.collections.array;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DirectBufferDoubleArray implements IDoubleArray {

    private final ByteBuffer values;
    private final int size;

    public DirectBufferDoubleArray(final int size) {
        this.values = ByteBuffer.allocateDirect(size * Double.BYTES);
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
