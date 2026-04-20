package de.invesdwin.util.collections.array.large.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateIntegerLargeArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class IntegerPrimitiveAsLargeArray implements IIntegerLargeArray {

    private final IIntegerPrimitiveArray values;

    public IntegerPrimitiveAsLargeArray(final IIntegerPrimitiveArray values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final long index, final int value) {
        values.set(ByteBuffers.checkedCast(index), value);
    }

    @Override
    public int get(final long index) {
        return values.get(ByteBuffers.checkedCast(index));
    }

    @Override
    public long size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public IIntegerLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateIntegerLargeArray(this, fromIndex, length);
    }

    public int[] asArray() {
        return values.asArray();
    }

    @Override
    public int[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return values.asArray(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    public int[] asArrayCopy() {
        return values.asArrayCopy();
    }

    @Override
    public int[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return values.asArrayCopy(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    @Override
    public void getIntegers(final long srcPos, final IIntegerLargeArray dest, final long destPos, final long length) {
        if (dest instanceof IntegerPrimitiveAsLargeArray) {
            final IntegerPrimitiveAsLargeArray cDest = ((IntegerPrimitiveAsLargeArray) dest);
            values.getIntegers(ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
                    ByteBuffers.checkedCast(length));
        } else {
            for (long i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        for (int i = 0; i < size(); i++) {
            buffer.putDouble(i * Integer.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Integer.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
