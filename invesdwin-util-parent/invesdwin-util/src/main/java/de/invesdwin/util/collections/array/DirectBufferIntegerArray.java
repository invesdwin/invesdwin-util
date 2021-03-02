package de.invesdwin.util.collections.array;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DirectBufferIntegerArray implements IIntegerArray {

    private final ByteBuffer values;
    private final int size;

    public DirectBufferIntegerArray(final int size) {
        this.values = ByteBuffer.allocateDirect(size * Integer.BYTES);
        this.size = size;
    }

    @Override
    public void set(final int index, final int value) {
        values.putInt(index, value);
    }

    @Override
    public int get(final int index) {
        return values.getInt(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public IIntegerArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] asArray() {
        throw new UnsupportedOperationException();
    }

}
