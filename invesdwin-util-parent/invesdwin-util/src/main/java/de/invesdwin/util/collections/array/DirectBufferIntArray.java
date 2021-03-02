package de.invesdwin.util.collections.array;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DirectBufferIntArray implements IIntArray {

    private final ByteBuffer values;
    private final int size;

    public DirectBufferIntArray(final int size) {
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

}
