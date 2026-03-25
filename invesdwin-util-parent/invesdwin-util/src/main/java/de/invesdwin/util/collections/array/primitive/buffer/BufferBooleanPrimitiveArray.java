package de.invesdwin.util.collections.array.primitive.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimtiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.BitSetBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.LongArrayPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.LongArrayPrimitiveBitSetBase;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateBooleanPrimitiveArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferBooleanPrimitiveArray implements IBooleanPrimtiveArray {

    public static final int LENGTH_INDEX = 0;
    public static final int LENGTH_SIZE = Integer.BYTES;

    public static final int ARRAY_INDEX = LENGTH_INDEX + LENGTH_SIZE;

    private final IByteBuffer buffer;
    private final BitSetBooleanPrimitiveArray delegate;

    public BufferBooleanPrimitiveArray(final IByteBuffer buffer) {
        this.buffer = buffer;
        final int expectedSize = buffer.getInt(LENGTH_INDEX);
        final LongArrayPrimitiveBitSetBase bitSet = new LongArrayPrimitiveBitSetBase(new BufferLongPrimitiveArray(buffer.sliceFrom(ARRAY_INDEX)),
                expectedSize);
        this.delegate = new BitSetBooleanPrimitiveArray(new LongArrayPrimitiveBitSet(bitSet, expectedSize));
        assert buffer.getId() == delegate.getId();
    }

    public BufferBooleanPrimitiveArray(final IByteBuffer buffer, final int expectedSize) {
        this.buffer = buffer;
        final LongArrayPrimitiveBitSetBase bitSet = new LongArrayPrimitiveBitSetBase(new BufferLongPrimitiveArray(buffer), expectedSize);
        this.delegate = new BitSetBooleanPrimitiveArray(new LongArrayPrimitiveBitSet(bitSet, expectedSize));
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    public BitSetBooleanPrimitiveArray getDelegate() {
        return delegate;
    }

    @Override
    public void set(final int index, final boolean value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        delegate.set(index, value);
    }

    @Override
    public boolean get(final int index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public IBooleanPrimtiveArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanPrimitiveArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return delegate.asArray();
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return delegate.asArray(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy() {
        return delegate.asArrayCopy();
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanPrimtiveArray dest, final int destPos, final int length) {
        delegate.getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer dst) throws IOException {
        dst.putInt(LENGTH_INDEX, delegate.getBitSet().size());
        buffer.getBytes(0, dst, ARRAY_INDEX, buffer.capacity());
        return ARRAY_INDEX + buffer.capacity();
    }

    @Override
    public int getBufferLength() {
        return ARRAY_INDEX + buffer.capacity();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

}
