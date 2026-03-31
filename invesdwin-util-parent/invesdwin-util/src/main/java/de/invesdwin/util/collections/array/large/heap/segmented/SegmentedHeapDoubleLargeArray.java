package de.invesdwin.util.collections.array.large.heap.segmented;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.heap.HeapBooleanLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateDoubleLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SegmentedHeapDoubleLargeArray implements IDoubleLargeArray {

    public static final long SEGMENT_SIZE = HeapBooleanLargeArray.MAX_SIZE;

    private final double[][] segments;
    private final long size;
    private final int segmentCount;

    public SegmentedHeapDoubleLargeArray(final long size) {
        if (size <= RoaringLargeBitSet.MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Use RoaringLargeBitSet directly for sizes <= " + RoaringLargeBitSet.MAX_SIZE);
        }
        this.size = size;
        this.segmentCount = (int) ((size + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
        this.segments = new double[segmentCount][];
        for (int i = 0; i < segmentCount; i++) {
            final long segmentSize = Math.min(SEGMENT_SIZE, size - (i * SEGMENT_SIZE));
            segments[i] = new double[ByteBuffers.checkedCast(segmentSize)];
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
            final double[] segment = segments[i];
            Arrays.fill(segment, 0);
        }
    }

    @Override
    public double get(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        return segments[segmentIndex][segmentOffset];
    }

    @Override
    public void set(final long index, final double value) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        segments[segmentIndex][segmentOffset] = value;
    }

    @Override
    public IDoubleLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateDoubleLargeArray(this, fromIndex, length);
    }

    @Override
    public double[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public double[] asArrayCopy(final long fromIndex, final int length) {
        final double[] array = new double[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getDoubles(final long srcPos, final IDoubleLargeArray dest, final long destPos, final long length) {
        for (long i = 0; i < length; i++) {
            dest.set(destPos + i, get(srcPos + i));
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        for (long i = 0; i < size(); i++) {
            buffer.putDouble(i, get(i));
        }
        return getBufferLength();
    }

    @Override
    public long getBufferLength() {
        return size() * Double.BYTES;
    }

}
