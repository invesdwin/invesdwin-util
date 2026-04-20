package de.invesdwin.util.collections.array.primitive.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateLongPrimitiveArray;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class LongLargeAsPrimitiveArray implements ILongPrimitiveArray {

    private final ILongLargeArray values;

    public LongLargeAsPrimitiveArray(final ILongLargeArray values) {
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
    public void set(final int index, final long value) {
        values.set(index, value);
    }

    @Override
    public long get(final int index) {
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
    public ILongPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateLongPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public long[] asArray() {
        return values.asArray(0, size());
    }

    @Override
    public long[] asArray(final int fromIndex, final int length) {
        return values.asArray(fromIndex, length);
    }

    @Override
    public long[] asArrayCopy() {
        return values.asArrayCopy(0, size());
    }

    @Override
    public long[] asArrayCopy(final int fromIndex, final int length) {
        return values.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getLongs(final int srcPos, final ILongPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof LongLargeAsPrimitiveArray) {
            final LongLargeAsPrimitiveArray cDest = ((LongLargeAsPrimitiveArray) dest);
            values.getLongs(srcPos, cDest.values, destPos, length);
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
            buffer.putDouble(i * Long.BYTES, get(i));
        }
        return getBufferLength();
    }

    @Override
    public int getBufferLength() {
        return size() * Long.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
