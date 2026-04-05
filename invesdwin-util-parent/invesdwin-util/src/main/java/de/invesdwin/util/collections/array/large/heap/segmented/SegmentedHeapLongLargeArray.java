package de.invesdwin.util.collections.array.large.heap.segmented;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.heap.HeapLongLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateLongLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SegmentedHeapLongLargeArray implements ILongLargeArray {

    public static final long SEGMENT_SIZE = HeapLongLargeArray.MAX_SIZE;

    private final long[][] segments;
    private final long size;
    private final int segmentCount;

    public SegmentedHeapLongLargeArray(final long size) {
        if (size <= RoaringLargeBitSet.MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Use RoaringLargeBitSet directly for sizes <= " + RoaringLargeBitSet.MAX_SIZE);
        }
        this.size = size;
        this.segmentCount = (int) ((size + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
        this.segments = new long[segmentCount][];
        for (int i = 0; i < segmentCount; i++) {
            final long segmentSize = Longs.min(SEGMENT_SIZE, size - (i * SEGMENT_SIZE));
            segments[i] = new long[ByteBuffers.checkedCast(segmentSize)];
        }
    }

    private int getSegmentIndex(final long index) {
        return (int) (index / SEGMENT_SIZE);
    }

    private int getSegmentOffset(final long index) {
        return (int) (index % SEGMENT_SIZE);
    }

    @Override
    public int getId() {
        return System.identityHashCode(this);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < segments.length; i++) {
            final long[] segment = segments[i];
            Arrays.fill(segment, 0);
        }
    }

    @Override
    public long get(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        return segments[segmentIndex][segmentOffset];
    }

    @Override
    public void set(final long index, final long value) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        segments[segmentIndex][segmentOffset] = value;
    }

    @Override
    public ILongLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateLongLargeArray(this, fromIndex, length);
    }

    @Override
    public long[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public long[] asArrayCopy(final long fromIndex, final int length) {
        final long[] array = new long[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getLongs(final long srcPos, final ILongLargeArray dest, final long destPos, final long length) {
        for (long i = 0; i < length; i++) {
            dest.set(destPos + i, get(srcPos + i));
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        for (long i = 0; i < size(); i++) {
            buffer.putLong(i, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Long.BYTES;
    }

}
