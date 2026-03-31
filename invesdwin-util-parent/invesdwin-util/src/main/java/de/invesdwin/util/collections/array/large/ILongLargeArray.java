package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.ILongLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.empty.EmptyLongLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapLongLargeArray;
import de.invesdwin.util.collections.array.large.heap.segmented.SegmentedHeapLongLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

public interface ILongLargeArray extends ILargeArray, ILongLargeArrayAccessor {

    void set(long index, long value);

    ILongLargeArray slice(long fromIndex, long length);

    long[] asArray(long fromIndex, int length);

    long[] asArrayCopy(long fromIndex, int length);

    void getLongs(long srcPos, ILongLargeArray dest, long destPos, long length);

    static ILongLargeArray newInstance(final long size) {
        if (size == 0) {
            return EmptyLongLargeArray.INSTANCE;
        }
        //plain arrays are significantly faster than direct buffers
        if (size <= HeapLongLargeArray.MAX_SIZE) {
            return new HeapLongLargeArray(ByteBuffers.checkedCast(size));
        } else {
            return new SegmentedHeapLongLargeArray(size);
        }
    }

    static ILongLargeArray newInstance(final long[] values) {
        if (values.length == 0) {
            return EmptyLongLargeArray.INSTANCE;
        }
        return new HeapLongLargeArray(values);
    }

}
