package de.invesdwin.util.collections.array.primitive.heap;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.BitSetBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateBooleanPrimitiveArray;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapBooleanPrimitiveArray implements IBooleanPrimitiveArray {

    private final boolean[] values;

    public HeapBooleanPrimitiveArray(final int size) {
        this.values = new boolean[size];
    }

    public HeapBooleanPrimitiveArray(final boolean[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final int index, final boolean value) {
        values[index] = value;
    }

    @Override
    public boolean get(final int index) {
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
    public IBooleanPrimitiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return values;
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public boolean[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanPrimitiveArray dest, final int destPos, final int length) {
        if (dest instanceof BitSetBooleanPrimitiveArray) {
            final HeapBooleanPrimitiveArray cDest = ((HeapBooleanPrimitiveArray) dest);
            System.arraycopy(values, srcPos, cDest.values, destPos, length);
            return;
        } else {
            for (int i = 0; i < length; i++) {
                dest.set(destPos + i, values[srcPos + i]);
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
        Arrays.fill(values, false);
    }

}
