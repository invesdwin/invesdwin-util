package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IGenericLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.empty.EmptyGenericLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapGenericLargeArray;
import de.invesdwin.util.collections.array.large.heap.segmented.SegmentedHeapGenericLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

public interface IGenericLargeArray<E> extends ILargeArray, IGenericLargeArrayAccessor<E> {

    void set(long index, E value);

    IGenericLargeArray<E> slice(long fromIndex, long length);

    /**
     * WARNING: this operation will fail for very large arrays that exceed the maximum array size of the JVM. Use with
     * caution.
     */
    default E[] asArray() {
        return asArray(0, ByteBuffers.checkedCast(size()));
    }

    E[] asArray(long fromIndex, int length);

    /**
     * WARNING: this operation will fail for very large arrays that exceed the maximum array size of the JVM. Use with
     * caution.
     */
    default E[] asArrayCopy() {
        return asArrayCopy(0, ByteBuffers.checkedCast(size()));
    }

    E[] asArrayCopy(long fromIndex, int length);

    void getGenerics(long srcPos, IGenericLargeArray<E> dest, long destPos, long length);

    static <T> IGenericLargeArray<T> newInstance(final Class<T> type, final long size) {
        if (size == 0) {
            return EmptyGenericLargeArray.getInstance();
        } else if (size <= Integer.MAX_VALUE) {
            return new HeapGenericLargeArray<T>(type, ByteBuffers.checkedCast(size));
        } else {
            return new SegmentedHeapGenericLargeArray<T>(type, size);
        }
    }

    static <T> IGenericLargeArray<T> newInstance(final T[] emptyArray, final long size) {
        if (size == 0) {
            return EmptyGenericLargeArray.getInstance();
        } else if (size <= Integer.MAX_VALUE) {
            return new HeapGenericLargeArray<T>(emptyArray, ByteBuffers.checkedCast(size));
        } else {
            return new SegmentedHeapGenericLargeArray<T>(emptyArray, size);
        }
    }

    static <T> IGenericLargeArray<T> newInstance(final T[] values) {
        if (values.length == 0) {
            return EmptyGenericLargeArray.getInstance();
        }
        return new HeapGenericLargeArray<T>(values);
    }

}
