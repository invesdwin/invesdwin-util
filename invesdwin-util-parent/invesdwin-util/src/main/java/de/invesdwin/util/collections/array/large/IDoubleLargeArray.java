package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IDoubleLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.empty.EmptyDoubleLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapDoubleLargeArray;
import de.invesdwin.util.collections.array.large.heap.segmented.SegmentedHeapDoubleLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

public interface IDoubleLargeArray extends ILargeArray, IDoubleLargeArrayAccessor {

    void set(long index, double value);

    IDoubleLargeArray slice(long fromIndex, long length);

    double[] asArray(long fromIndex, int length);

    double[] asArrayCopy(long fromIndex, int length);

    void getDoubles(long srcPos, IDoubleLargeArray dest, long destPos, long length);

    static IDoubleLargeArray newInstance(final long size) {
        if (size == 0) {
            return EmptyDoubleLargeArray.INSTANCE;
        }
        //plain arrays are significantly faster than direct buffers
        if (size <= HeapDoubleLargeArray.MAX_SIZE) {
            return new HeapDoubleLargeArray(ByteBuffers.checkedCast(size));
        } else {
            return new SegmentedHeapDoubleLargeArray(size);
        }
    }

    static IDoubleLargeArray newInstance(final double[] values) {
        if (values.length == 0) {
            return EmptyDoubleLargeArray.INSTANCE;
        }
        return new HeapDoubleLargeArray(values);
    }

}
