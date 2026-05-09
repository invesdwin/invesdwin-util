package de.invesdwin.util.collections.array.primitive.large;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.slice.SliceDelegateGenericPrimitiveArray;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class GenericLargeAsPrimitiveArray<E> implements IGenericPrimitiveArray<E> {

    private final IGenericLargeArray<E> values;

    public GenericLargeAsPrimitiveArray(final IGenericLargeArray<E> values) {
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
    public void set(final int index, final E value) {
        values.set(index, value);
    }

    @Override
    public E get(final int index) {
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
    public IGenericPrimitiveArray<E> slice(final int fromIndex, final int length) {
        return new SliceDelegateGenericPrimitiveArray<E>(this, fromIndex, length);
    }

    @Override
    public E[] asArray() {
        return values.asArray(0, size());
    }

    @Override
    public E[] asArray(final int fromIndex, final int length) {
        return values.asArray(fromIndex, length);
    }

    @Override
    public E[] asArrayCopy() {
        return values.asArrayCopy(0, size());
    }

    @Override
    public E[] asArrayCopy(final int fromIndex, final int length) {
        return values.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getGenerics(final int srcPos, final IGenericPrimitiveArray<E> dest, final int destPos,
            final int length) {
        if (dest instanceof GenericLargeAsPrimitiveArray) {
            final GenericLargeAsPrimitiveArray<E> cDest = ((GenericLargeAsPrimitiveArray<E>) dest);
            values.getGenerics(srcPos, cDest.values, destPos, length);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        values.clear();
    }

}
