package de.invesdwin.util.collections.array.large.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateLongLargeArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class LongPrimitiveAsLargeArray implements ILongLargeArray {

    private final ILongPrimitiveArray values;

    public LongPrimitiveAsLargeArray(final ILongPrimitiveArray values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final long index, final long value) {
        values.set(ByteBuffers.checkedCast(index), value);
    }

    @Override
    public long get(final long index) {
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
    public ILongLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateLongLargeArray(this, fromIndex, length);
    }

    public long[] asArray() {
        return values.asArray();
    }

    @Override
    public long[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return values.asArray(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    public long[] asArrayCopy() {
        return values.asArrayCopy();
    }

    @Override
    public long[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return values.asArrayCopy(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    @Override
    public void getLongs(final long srcPos, final ILongLargeArray dest, final long destPos, final long length) {
        if (dest instanceof LongPrimitiveAsLargeArray) {
            final LongPrimitiveAsLargeArray cDest = ((LongPrimitiveAsLargeArray) dest);
            values.getLongs(ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
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
            buffer.putDouble(i * Long.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Long.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
