package de.invesdwin.util.collections.array.large.heap.segmented;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.heap.HeapGenericLargeArray;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateGenericLargeArray;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SegmentedHeapGenericLargeArray<E> implements IGenericLargeArray<E> {

    public static final long SEGMENT_SIZE = HeapGenericLargeArray.MAX_SIZE;

    private final Class<E> type;
    private final E[][] segments;
    private final long size;
    private final int segmentCount;

    @SuppressWarnings("unchecked")
    public SegmentedHeapGenericLargeArray(final Class<E> type, final long size) {
        this.type = type;
        if (size <= RoaringLargeBitSet.MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Use RoaringLargeBitSet directly for sizes <= " + RoaringLargeBitSet.MAX_SIZE);
        }
        this.size = size;
        this.segmentCount = (int) ((size + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
        this.segments = (E[][]) Arrays.newInstance(type, segmentCount, 0);
        for (int i = 0; i < segmentCount; i++) {
            final long segmentSize = Math.min(SEGMENT_SIZE, size - (i * SEGMENT_SIZE));
            segments[i] = (E[]) Arrays.newInstance(type, ByteBuffers.checkedCast(segmentSize));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SegmentedHeapGenericLargeArray(final E[] emptyArray, final long size) {
        this((Class) emptyArray.getClass().getComponentType(), size);
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
            final E[] segment = segments[i];
            Arrays.fill(segment, 0);
        }
    }

    @Override
    public E get(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        return segments[segmentIndex][segmentOffset];
    }

    @Override
    public void set(final long index, final E value) {
        final int segmentIndex = getSegmentIndex(index);
        final int segmentOffset = getSegmentOffset(index);
        segments[segmentIndex][segmentOffset] = value;
    }

    @Override
    public IGenericLargeArray<E> slice(final long fromIndex, final long length) {
        return new SliceDelegateGenericLargeArray<E>(this, fromIndex, length);
    }

    @Override
    public E[] asArray(final long fromIndex, final int length) {
        return asArrayCopy(fromIndex, length);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArrayCopy(final long fromIndex, final int length) {
        final E[] array = (E[]) Arrays.newInstance(type, length);
        int j = 0;
        for (long i = fromIndex; j < length; i++, j++) {
            array[j] = get(i);
        }
        return array;
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<E> dest, final long destPos,
            final long length) {
        for (long i = 0; i < length; i++) {
            dest.set(destPos + i, get(srcPos + i));
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

}
