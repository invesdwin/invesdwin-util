package de.invesdwin.util.collections.array.large.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateGenericLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class HeapGenericLargeArray<E> implements IGenericLargeArray<E> {

    public static final int MAX_SIZE = Integer.MAX_VALUE;

    private final E[] values;

    @SuppressWarnings("unchecked")
    public HeapGenericLargeArray(final Class<E> type, final int size) {
        this.values = (E[]) Arrays.newInstance(type, size);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public HeapGenericLargeArray(final E[] emptyArray, final int size) {
        this((Class) emptyArray.getClass().getComponentType(), size);
    }

    public HeapGenericLargeArray(final E[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final long index, final E value) {
        values[ByteBuffers.checkedCast(index)] = value;
    }

    @Override
    public E get(final long index) {
        return values[ByteBuffers.checkedCast(index)];
    }

    @Override
    public long size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IGenericLargeArray<E> slice(final long fromIndex, final long length) {
        return new SliceDelegateGenericLargeArray<E>(this, fromIndex, length);
    }

    public E[] asArray() {
        return values;
    }

    @Override
    public E[] asArray(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    public E[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public E[] asArrayCopy(final long fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, ByteBuffers.checkedCast(fromIndex),
                    ByteBuffers.checkedCast(fromIndex + length));
        }
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<E> dest, final long destPos,
            final long length) {
        if (dest instanceof HeapGenericLargeArray) {
            final HeapGenericLargeArray<E> cDest = ((HeapGenericLargeArray<E>) dest);
            System.arraycopy(values, ByteBuffers.checkedCast(srcPos), cDest.values, ByteBuffers.checkedCast(destPos),
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
        Arrays.fill(values, null);
    }

}
