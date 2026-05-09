package de.invesdwin.util.collections.array.large.primitive;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.BitSetBooleanLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateBooleanLargeArray;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BooleanPrimitiveAsLargeArray implements IBooleanLargeArray {

    private final IBooleanPrimitiveArray values;

    public BooleanPrimitiveAsLargeArray(final IBooleanPrimitiveArray values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final long index, final boolean value) {
        values.set(ByteBuffers.checkedCast(index), value);
    }

    @Override
    public boolean get(final long index) {
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
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateBooleanLargeArray(this, fromIndex, length);
    }

    public boolean[] asArray() {
        return values.asArray();
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return values.asArray(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    public boolean[] asArrayCopy() {
        return values.asArrayCopy();
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return values.asArrayCopy(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
        if (dest instanceof BooleanPrimitiveAsLargeArray) {
            final BooleanPrimitiveAsLargeArray cDest = ((BooleanPrimitiveAsLargeArray) dest);
            values.getBooleans(ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
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
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        //always save as long array
        final BitSetBooleanLargeArray delegate = new BitSetBooleanLargeArray(size());
        for (long i = 0; i < size(); i++) {
            delegate.set(i, get(i));
        }
        return delegate.getBuffer(buffer);
    }

    @Override
    public long getBufferLength() {
        return (BitSets.wordIndex(size() - 1) + 1) * Long.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
