package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@NotThreadSafe
public class DirectBufferIntegerArray implements IIntegerArray {

    private final IByteBuffer values;
    private final int size;

    public DirectBufferIntegerArray(final int size) {
        this.values = ByteBuffers.allocateDirect(size * Integer.BYTES);
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
