package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IIntegerLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.empty.EmptyIntegerLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapIntegerLargeArray;
import de.invesdwin.util.collections.array.large.heap.segmented.SegmentedHeapIntegerLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

public interface IIntegerLargeArray extends ILargeArray, IIntegerLargeArrayAccessor {

    void set(long index, int value);

    IIntegerLargeArray slice(long fromIndex, long length);

    /**
     * WARNING: this operation will fail for very large arrays that exceed the maximum array size of the JVM. Use with
     * caution.
     */
    default int[] asArray() {
        return asArray(0, ByteBuffers.checkedCast(size()));
    }

    int[] asArray(long fromIndex, int length);

    /**
     * WARNING: this operation will fail for very large arrays that exceed the maximum array size of the JVM. Use with
     * caution.
     */
    default int[] asArrayCopy() {
        return asArrayCopy(0, ByteBuffers.checkedCast(size()));
    }

    int[] asArrayCopy(long fromIndex, int length);

    void getIntegers(long srcPos, IIntegerLargeArray dest, long destPos, long length);

    static IIntegerLargeArray newInstance(final long size) {
        if (size == 0) {
            return EmptyIntegerLargeArray.INSTANCE;
        }
        if (size <= HeapIntegerLargeArray.MAX_SIZE) {
            return new HeapIntegerLargeArray(ByteBuffers.checkedCast(size));
        } else {
            return new SegmentedHeapIntegerLargeArray(size);
        }
    }

    static IIntegerLargeArray newInstance(final int[] values) {
        if (values.length == 0) {
            return EmptyIntegerLargeArray.INSTANCE;
        }
        return new HeapIntegerLargeArray(values);
    }

}
