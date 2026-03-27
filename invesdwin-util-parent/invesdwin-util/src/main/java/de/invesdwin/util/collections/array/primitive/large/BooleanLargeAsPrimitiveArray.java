package de.invesdwin.util.collections.array.primitive.large;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.BitSetBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateBooleanPrimitiveArray;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BooleanLargeAsPrimitiveArray implements IBooleanPrimitiveArray {

    private final IBooleanLargeArray values;

    public BooleanLargeAsPrimitiveArray(final IBooleanLargeArray values) {
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
    public void set(final int index, final boolean value) {
        values.set(index, value);
    }

    @Override
    public boolean get(final int index) {
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
    public IBooleanPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return values.asArray(0, size());
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return values.asArray(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy() {
        return values.asArrayCopy(0, size());
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        return values.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof BooleanLargeAsPrimitiveArray) {
            final BooleanLargeAsPrimitiveArray cDest = ((BooleanLargeAsPrimitiveArray) dest);
            values.getBooleans(srcPos, cDest.values, destPos, length);
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
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        //always save as long array
        final BitSetBooleanPrimitiveArray delegate = new BitSetBooleanPrimitiveArray(size());
        for (int i = 0; i < size(); i++) {
            delegate.set(i, get(i));
        }
        return delegate.getBuffer(buffer);
    }

    @Override
    public int getBufferLength() {
        return (BitSets.wordIndex(size() - 1) + 1) * Long.BYTES;
    }

    @Override
    public void clear() {
        values.clear();
    }

}
