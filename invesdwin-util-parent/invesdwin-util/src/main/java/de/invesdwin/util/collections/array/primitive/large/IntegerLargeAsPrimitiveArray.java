package de.invesdwin.util.collections.array.primitive.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateIntegerPrimitiveArray;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class IntegerLargeAsPrimitiveArray implements IIntegerPrimitiveArray {

    private final IIntegerLargeArray values;

    public IntegerLargeAsPrimitiveArray(final IIntegerLargeArray values) {
        this.values = values;
        if (Throwables.isDebugStackTraceEnabled()) {
            ByteBuffers.checkedCast(values.size());
        }
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final int index, final int value) {
        values.set(index, value);
    }

    @Override
    public int get(final int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return ByteBuffers.checkedCastNoOverflow(values.size());
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public IIntegerPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateIntegerPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public int[] asArray() {
        return values.asArray(0, size());
    }

    @Override
    public int[] asArray(final int fromIndex, final int length) {
        return values.asArray(fromIndex, length);
    }

    @Override
    public int[] asArrayCopy() {
        return values.asArrayCopy(0, size());
    }

    @Override
    public int[] asArrayCopy(final int fromIndex, final int length) {
        return values.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getIntegers(final int srcPos, final IIntegerPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof IntegerLargeAsPrimitiveArray) {
            final IntegerLargeAsPrimitiveArray cDest = ((IntegerLargeAsPrimitiveArray) dest);
            values.getIntegers(srcPos, cDest.values, destPos, length);
        } else {
            for (int i = 0; i < length; i++) {
                dest.set(destPos + i, get(srcPos + i));
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
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
        values.clear();
    }

}
