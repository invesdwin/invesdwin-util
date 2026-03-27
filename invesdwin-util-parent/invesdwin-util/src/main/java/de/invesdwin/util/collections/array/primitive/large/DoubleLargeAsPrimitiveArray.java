package de.invesdwin.util.collections.array.primitive.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateDoublePrimitiveArray;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class DoubleLargeAsPrimitiveArray implements IDoublePrimitiveArray {

    private final IDoubleLargeArray values;

    public DoubleLargeAsPrimitiveArray(final IDoubleLargeArray values) {
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
    public void set(final int index, final double value) {
        values.set(index, value);
    }

    @Override
    public double get(final int index) {
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
    public IDoublePrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateDoublePrimitiveArray(this, fromIndex, length);
    }

    @Override
    public double[] asArray() {
        return values.asArray(0, size());
    }

    @Override
    public double[] asArray(final int fromIndex, final int length) {
        return values.asArray(fromIndex, length);
    }

    @Override
    public double[] asArrayCopy() {
        return values.asArrayCopy(0, size());
    }

    @Override
    public double[] asArrayCopy(final int fromIndex, final int length) {
        return values.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getDoubles(final int srcPos, final IDoublePrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof DoubleLargeAsPrimitiveArray) {
            final DoubleLargeAsPrimitiveArray cDest = ((DoubleLargeAsPrimitiveArray) dest);
            values.getDoubles(srcPos, cDest.values, destPos, length);
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
            buffer.putDouble(i * Double.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public int getBufferLength() {
        return size() * Double.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
