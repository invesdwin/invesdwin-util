package de.invesdwin.util.collections.array.large.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateGenericLargeArray;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class GenericPrimitiveAsLargeArray<E> implements IGenericLargeArray<E> {

    private final IGenericPrimitiveArray<E> values;

    public GenericPrimitiveAsLargeArray(final IGenericPrimitiveArray<E> values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return values.getId();
    }

    @Override
    public void set(final long index, final E value) {
        values.set(ByteBuffers.checkedCast(index), value);
    }

    @Override
    public E get(final long index) {
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
    public IGenericLargeArray<E> slice(final long fromIndex, final long length) {
        return new SliceDelegateGenericLargeArray<E>(this, fromIndex, length);
    }

    public E[] asArray() {
        return values.asArray();
    }

    @Override
    public E[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return values.asArray(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    public E[] asArrayCopy() {
        return values.asArrayCopy();
    }

    @Override
    public E[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return values.asArrayCopy(ByteBuffers.checkedCast(fromIndex), length);
        }
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<E> dest, final long destPos,
            final long length) {
        if (dest instanceof GenericPrimitiveAsLargeArray) {
            final GenericPrimitiveAsLargeArray<E> cDest = ((GenericPrimitiveAsLargeArray<E>) dest);
            values.getGenerics(ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
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
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        values.clear();
    }

}
