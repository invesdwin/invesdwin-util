package de.invesdwin.util.collections.array.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.SliceDelegateIntegerArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapIntegerArray implements IIntegerArray {

    private final int[] values;

    public HeapIntegerArray(final int size) {
        this.values = new int[size];
    }

    public HeapIntegerArray(final int[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final int value) {
        values[index] = value;
    }

    @Override
    public int get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IIntegerArray slice(final int fromIndex, final int length) {
        return new SliceDelegateIntegerArray(this, fromIndex, length);
    }

    @Override
    public int[] asArray() {
        return values;
    }

    @Override
    public int[] asArray(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public int[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public int[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getIntegers(final int srcPos, final IIntegerArray dest, final int destPos, final int length) {
        final HeapIntegerArray cDest = ((HeapIntegerArray) dest);
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
