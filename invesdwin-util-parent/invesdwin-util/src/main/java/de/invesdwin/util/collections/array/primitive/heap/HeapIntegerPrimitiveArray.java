package de.invesdwin.util.collections.array.primitive.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateIntegerPrimitiveArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapIntegerPrimitiveArray implements IIntegerPrimitiveArray {

    private final int[] values;

    public HeapIntegerPrimitiveArray(final int size) {
        this.values = new int[size];
    }

    public HeapIntegerPrimitiveArray(final int[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
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
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IIntegerPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateIntegerPrimitiveArray(this, fromIndex, length);
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
    public void getIntegers(final int srcPos, final IIntegerPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof HeapIntegerPrimitiveArray) {
            final HeapIntegerPrimitiveArray cDest = ((HeapIntegerPrimitiveArray) dest);
            System.arraycopy(values, srcPos, cDest.values, destPos, length);
        } else {
            for (int i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putDouble(i * Integer.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public int getBufferLength() {
        return size() * Integer.BYTES;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0);
    }

}
