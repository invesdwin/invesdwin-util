package de.invesdwin.util.collections.array.primitive.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateIntegerPrimitiveArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferIntegerPrimitiveArray implements IIntegerPrimitiveArray {

    private final IByteBuffer buffer;

    public BufferIntegerPrimitiveArray(final IByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    @Override
    public void set(final int index, final int value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        buffer.putInt(index * Integer.BYTES, value);
    }

    @Override
    public int get(final int index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return buffer.getInt(index * Integer.BYTES);
    }

    @Override
    public int size() {
        return buffer.capacity() / Integer.BYTES;
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public IIntegerPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateIntegerPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public int[] asArray() {
        return asArrayCopy();
    }

    @Override
    public int[] asArray(final int fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public int[] asArrayCopy() {
        return asArrayCopy(0, size());
    }

    @Override
    public int[] asArrayCopy(final int fromIndex, final int length) {
        final int[] array = new int[length];
        for (int i = fromIndex; i < length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    @Override
    public void getIntegers(final int srcPos, final IIntegerPrimitiveArray dest, final int destPos, final int length) {
        final BufferIntegerPrimitiveArray cDest = ((BufferIntegerPrimitiveArray) dest);
        buffer.getBytes(srcPos * Integer.BYTES, cDest.buffer, destPos * Integer.BYTES, length * Integer.BYTES);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer dst) throws IOException {
        buffer.getBytes(0, dst, 0, buffer.capacity());
        return buffer.capacity();
    }

    @Override
    public int getBufferLength() {
        return buffer.capacity();
    }

    @Override
    public void clear() {
        buffer.clear();
    }

}
