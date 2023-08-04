package de.invesdwin.util.collections.array.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.SliceDelegateBooleanArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapBooleanArray implements IBooleanArray {

    private final boolean[] values;

    public HeapBooleanArray(final int size) {
        this.values = new boolean[size];
    }

    public HeapBooleanArray(final boolean[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final boolean value) {
        values[index] = value;
    }

    @Override
    public boolean get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IBooleanArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return values;
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public boolean[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
        final HeapBooleanArray cDest = ((HeapBooleanArray) dest);
        System.arraycopy(values, srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer) {
        System.out.println("TODO");
        return 0;
    }

}
