package de.invesdwin.util.collections.array.large.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateDoubleLargeArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class DoublePrimitiveAsLargeArray implements IDoubleLargeArray {

    private final IDoublePrimitiveArray values;

    public DoublePrimitiveAsLargeArray(final IDoublePrimitiveArray values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final long index, final double value) {
        values.set(ByteBuffers.checkedCast(index), value);
    }

    @Override
    public double get(final long index) {
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
    public IDoubleLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateDoubleLargeArray(this, fromIndex, length);
    }

    public double[] asArray() {
        return values.asArray();
    }

    @Override
    public double[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return values.asArray(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    public double[] asArrayCopy() {
        return values.asArrayCopy();
    }

    @Override
    public double[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return values.asArrayCopy(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    @Override
    public void getDoubles(final long srcPos, final IDoubleLargeArray dest, final long destPos, final long length) {
        if (dest instanceof DoublePrimitiveAsLargeArray) {
            final DoublePrimitiveAsLargeArray cDest = ((DoublePrimitiveAsLargeArray) dest);
            values.getDoubles(ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
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
        for (long i = 0; i < size(); i++) {
            buffer.putDouble(i * Double.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Double.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
