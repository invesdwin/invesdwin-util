package de.invesdwin.util.collections.array.large.heap.segmented;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.BitSetBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.heap.HeapBooleanLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateBooleanLargeArray;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SegmentedHeapBooleanLargeArray implements IBooleanLargeArray {

    public static final long SEGMENT_SIZE = HeapBooleanLargeArray.MAX_SIZE;

    private final boolean[][] segments;
    private final long size;
    private final int segmentCount;

    public SegmentedHeapBooleanLargeArray(final long size) {
        if (size <= RoaringLargeBitSet.MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Use RoaringLargeBitSet directly for sizes <= " + RoaringLargeBitSet.MAX_SIZE);
        }
        this.size = size;
        this.segmentCount = (int) ((size + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
        this.segments = new boolean[segmentCount][];
        for (int i = 0; i < segmentCount; i++) {
            final long segmentSize = Math.min(SEGMENT_SIZE, size - (i * SEGMENT_SIZE));
            segments[i] = new boolean[ByteBuffers.checkedCast(segmentSize)];
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
            final boolean[] segment = segments[i];
            Arrays.fill(segment, false);
        }
    }

    @Override
    public boolean get(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        return segments[segmentIndex][segmentOffset];
    }

    @Override
    public void set(final long index, final boolean value) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        segments[segmentIndex][segmentOffset] = value;
    }

    @Override
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateBooleanLargeArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        final boolean[] array = new boolean[length];
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
        for (long i = 0; i < length; i++) {
            dest.set(destPos + i, get(srcPos + i));
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        final BitSetBooleanLargeArray delegate = new BitSetBooleanLargeArray(size());
        for (long i = 0; i < size(); i++) {
            delegate.set(i, get(i));
        }
        return delegate.getBuffer(buffer);
    }

    @Override
    public long getBufferLength() {
        return (BitSets.wordIndex(size() - 1) + 1) * Long.BYTES;
    }

}
