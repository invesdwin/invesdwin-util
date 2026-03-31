package de.invesdwin.util.collections.array.large.bitset.roaring;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.bitset.EmptyLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SegmentedRoaringLargeBitSet implements ILargeBitSet {

    public static final long SEGMENT_SIZE = RoaringLargeBitSet.MAX_SIZE;

    private final RoaringLargeBitSet[] segments;
    private final long size;

    public SegmentedRoaringLargeBitSet(final long size) {
        if (size <= RoaringLargeBitSet.MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Use RoaringLargeBitSet directly for sizes <= " + RoaringLargeBitSet.MAX_SIZE);
        }
        this.size = size;
        final int segmentCount = (int) ((size + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
        this.segments = new RoaringLargeBitSet[segmentCount];

        for (int i = 0; i < segmentCount; i++) {
            final long segmentSize = Math.min(SEGMENT_SIZE, size - (i * SEGMENT_SIZE));
            segments[i] = new RoaringLargeBitSet(segmentSize);
        }
    }

    private SegmentedRoaringLargeBitSet(final RoaringLargeBitSet[] segments, final long size) {
        this.segments = segments;
        this.size = size;
    }

    private int getSegmentIndex(final long index) {
        return (int) (index / SEGMENT_SIZE);
    }

    private long getSegmentOffset(final long index) {
        return index % SEGMENT_SIZE;
    }

    @Override
    public int getId() {
        return System.identityHashCode(this);
    }

    @Override
    public void add(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final long segmentOffset = getSegmentOffset(index);
        segments[segmentIndex].add(segmentOffset);
    }

    @Override
    public void remove(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final long segmentOffset = getSegmentOffset(index);
        segments[segmentIndex].remove(segmentOffset);
    }

    @Override
    public boolean contains(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final long segmentOffset = getSegmentOffset(index);
        return segments[segmentIndex].contains(segmentOffset);
    }

    @Override
    public void flip(final long index) {
        final int segmentIndex = getSegmentIndex(index);
        final long segmentOffset = getSegmentOffset(index);
        segments[segmentIndex].flip(segmentOffset);
    }

    @Override
    public void flip(final long index, final long length) {
        final long endIndex = index + length;
        long currentIndex = index;

        while (currentIndex < endIndex) {
            final int segmentIndex = getSegmentIndex(currentIndex);
            final long segmentOffset = getSegmentOffset(currentIndex);
            final long segmentEnd = Math.min((segmentIndex + 1) * SEGMENT_SIZE, endIndex);
            final long clearLength = segmentEnd - currentIndex;

            segments[segmentIndex].flip(segmentOffset, clearLength);
            currentIndex = segmentEnd;
        }
    }

    @Override
    public ILargeBitSet optimize() {
        final boolean allEmpty = true;
        final RoaringLargeBitSet[] optimizedSegments = new RoaringLargeBitSet[segments.length];

        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            final ILargeBitSet optimized = segment.optimize();
            optimizedSegments[i] = toResultSegment(optimized);
        }

        if (allEmpty) {
            return EmptyLargeBitSet.INSTANCE;
        }

        return new SegmentedRoaringLargeBitSet(optimizedSegments, size);
    }

    private RoaringLargeBitSet toResultSegment(final ILargeBitSet segment) {
        if (segment instanceof RoaringLargeBitSet) {
            final RoaringLargeBitSet cSegment = (RoaringLargeBitSet) segment;
            return cSegment;
        } else {
            Assertions.checkTrue(segment.isEmpty());
            return new RoaringLargeBitSet(segment.size());
        }
    }

    private void assertSameSize(final ILargeBitSet other) {
        if (other.size() != size) {
            throw new IllegalArgumentException("Cannot combine " + SegmentedRoaringLargeBitSet.class.getSimpleName()
                    + " with an " + ILargeBitSet.class.getSimpleName() + " of different size [" + other.size()
                    + "] than this [" + size + "]");
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }

        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }

        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            assertSameSize(other);
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }

        // Create result segments
        final RoaringLargeBitSet[] resultSegments = newResultSegments();
        final SegmentedRoaringLargeBitSet wrapped = new SegmentedRoaringLargeBitSet(resultSegments, size);

        for (int j = 0; j < others.length; j++) {
            final ILargeBitSet other = others[j];
            if (other instanceof SegmentedRoaringLargeBitSet) {
                final SegmentedRoaringLargeBitSet segmentedOther = (SegmentedRoaringLargeBitSet) other;
                for (int i = 0; i < segments.length; i++) {
                    final ILargeBitSet resultSegment = resultSegments[i].and(segmentedOther.segments[i]);
                    resultSegments[i] = toResultSegment(resultSegment);
                }
            } else {
                // Use BitSets.andFast for non-segmented others
                for (int i = 0; i < segments.length; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    final long segmentEnd = Math.min(segmentStart + SEGMENT_SIZE, size);
                    BitSets.andRange(resultSegments[i], other, segmentStart, segmentEnd);
                }
            }
            if (wrapped.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }

        return wrapped;
    }

    private RoaringLargeBitSet[] newResultSegments() {
        final RoaringLargeBitSet[] resultSegments = new RoaringLargeBitSet[segments.length];
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            final RoaringBitmap bitSet = segment.getBitSet().clone();
            resultSegments[i] = new RoaringLargeBitSet(bitSet, segment.size());
        }
        return resultSegments;
    }

    private RoaringLargeBitSet[] newResultSegments(final int firstSegment, final int lastSegment) {
        final RoaringLargeBitSet[] resultSegments = new RoaringLargeBitSet[segments.length];
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            if (firstSegment <= i && i <= lastSegment) {
                final RoaringBitmap bitSet = segment.getBitSet().clone();
                resultSegments[i] = new RoaringLargeBitSet(bitSet, segment.size());
            } else {
                resultSegments[i] = new RoaringLargeBitSet(segment.size());
            }
        }
        return resultSegments;
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (fromInclusive == 0 && toExclusive >= size) {
            return and(others);
        }

        if (others == null || others.length == 0) {
            return this;
        }

        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }

        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            assertSameSize(other);
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }

        // Create result segments
        final int firstSegment = getSegmentIndex(fromInclusive);
        final int lastSegment = getSegmentIndex(toExclusive - 1);
        final RoaringLargeBitSet[] resultSegments = newResultSegments(firstSegment, lastSegment);
        final SegmentedRoaringLargeBitSet wrapped = new SegmentedRoaringLargeBitSet(resultSegments, size);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        for (int j = 0; j < others.length; j++) {
            final ILargeBitSet other = others[j];
            if (other instanceof SegmentedRoaringLargeBitSet) {
                final SegmentedRoaringLargeBitSet segmentedOther = (SegmentedRoaringLargeBitSet) other;
                for (int i = firstSegment; i <= lastSegment; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    final long rangeStart = Math.max(fromInclusive, segmentStart);
                    final long rangeEnd = Math.min(toExclusive, segmentStart + SEGMENT_SIZE);

                    if (rangeStart < rangeEnd) {
                        final ILargeBitSet resultSegment = resultSegments[i].andRange(rangeStart - segmentStart,
                                rangeEnd - segmentStart, segmentedOther.segments[i]);
                        resultSegments[i] = toResultSegment(resultSegment);
                    }
                }
            } else {
                // Use BitSets.andRangeFast for non-segmented others
                for (int i = firstSegment; i <= lastSegment; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    BitSets.andRange(resultSegments[i], other, Math.max(fromInclusive, segmentStart),
                            Math.min(toExclusive, segmentStart + SEGMENT_SIZE));
                }
            }
            if (wrapped.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }

        return wrapped;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            assertSameSize(other);
        }

        // Create result segments (copy of this)
        final RoaringLargeBitSet[] resultSegments = newResultSegments();

        for (int j = 0; j < others.length; j++) {
            final ILargeBitSet other = others[j];
            if (other instanceof SegmentedRoaringLargeBitSet) {
                final SegmentedRoaringLargeBitSet segmentedOther = (SegmentedRoaringLargeBitSet) other;
                for (int i = 0; i < segments.length; i++) {
                    final ILargeBitSet resultSegment = resultSegments[i].or(segmentedOther.segments[i]);
                    resultSegments[i] = toResultSegment(resultSegment);
                }
            } else {
                // Use BitSets.orFast for non-segmented others
                for (int i = 0; i < segments.length; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    final long segmentEnd = Math.min(segmentStart + SEGMENT_SIZE, size);
                    BitSets.orRange(resultSegments[i], other, segmentStart, segmentEnd);
                }
            }
        }

        return new SegmentedRoaringLargeBitSet(resultSegments, size);
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (fromInclusive == 0 && toExclusive >= size) {
            return or(others);
        }

        if (others == null || others.length == 0) {
            return this;
        }
        
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            assertSameSize(other);
        }

        // Create result segments (copy of this)
        final int firstSegment = getSegmentIndex(fromInclusive);
        final int lastSegment = getSegmentIndex(toExclusive - 1);
        final RoaringLargeBitSet[] resultSegments = newResultSegments(firstSegment, lastSegment);
        final SegmentedRoaringLargeBitSet wrapped = new SegmentedRoaringLargeBitSet(resultSegments, size);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        for (int j = 0; j < others.length; j++) {
            final ILargeBitSet other = others[j];
            if (other instanceof SegmentedRoaringLargeBitSet) {
                final SegmentedRoaringLargeBitSet segmentedOther = (SegmentedRoaringLargeBitSet) other;

                for (int i = firstSegment; i <= lastSegment; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    final long rangeStart = Math.max(fromInclusive, segmentStart);
                    final long rangeEnd = Math.min(toExclusive, segmentStart + SEGMENT_SIZE);

                    if (rangeStart < rangeEnd) {
                        final ILargeBitSet resultSegment = resultSegments[i].orRange(rangeStart - segmentStart,
                                rangeEnd - segmentStart, segmentedOther.segments[i]);
                        resultSegments[i] = toResultSegment(resultSegment);
                    }
                }
            } else {
                // Use BitSets.orRangeFast for non-segmented others
                for (int i = firstSegment; i <= lastSegment; i++) {
                    final long segmentStart = i * SEGMENT_SIZE;
                    BitSets.orRange(resultSegments[i], other, Math.max(fromInclusive, segmentStart),
                            Math.min(toExclusive, segmentStart + SEGMENT_SIZE));
                }
            }
        }

        return wrapped;
    }

    @Override
    public ILargeBitSet negate() {
        final RoaringLargeBitSet[] negatedSegments = new RoaringLargeBitSet[segments.length];
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            negatedSegments[i] = segment.negate();
        }
        return new SegmentedRoaringLargeBitSet(negatedSegments, size);
    }

    @Override
    public ILargeBitSet negateShallow() {
        return new ShallowNegatedLargeBitSet(this);
    }

    @Override
    public long getTrueCount() {
        long count = 0;
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            count += segment.getTrueCount();
        }
        return count;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            if (!segment.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return new SegmentedSkippingIndexProvider();
    }

    @Override
    public ILargeBitSet unwrap() {
        return this;
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        if (dest instanceof SegmentedRoaringLargeBitSet) {
            final SegmentedRoaringLargeBitSet segmentedDest = (SegmentedRoaringLargeBitSet) dest;
            // Handle segment-to-segment copy
            long remainingLength = length;
            long currentSrcPos = srcPos;
            long currentDestPos = destPos;

            while (remainingLength > 0) {
                final int currentSrcSegment = getSegmentIndex(currentSrcPos);
                final int currentDestSegment = segmentedDest.getSegmentIndex(currentDestPos);
                final long currentSrcOffset = getSegmentOffset(currentSrcPos);
                final long currentDestOffset = segmentedDest.getSegmentOffset(currentDestPos);

                final long srcSegmentEnd = (currentSrcSegment + 1) * SEGMENT_SIZE;
                final long destSegmentEnd = (currentDestSegment + 1) * SEGMENT_SIZE;
                final long srcRemainingInSegment = Math.min(srcSegmentEnd - currentSrcPos, remainingLength);
                final long destRemainingInSegment = Math.min(destSegmentEnd - currentDestPos, remainingLength);
                final long copyLength = Math.min(srcRemainingInSegment, destRemainingInSegment);

                segments[currentSrcSegment].getBooleans(currentSrcOffset, segmentedDest.segments[currentDestSegment],
                        currentDestOffset, copyLength);

                currentSrcPos += copyLength;
                currentDestPos += copyLength;
                remainingLength -= copyLength;
            }
        } else {
            // Handle segment-to-single-bitset copy
            long remainingLength = length;
            long currentSrcPos = srcPos;
            long currentDestPos = destPos;

            while (remainingLength > 0) {
                final int currentSrcSegment = getSegmentIndex(currentSrcPos);
                final long currentSrcOffset = getSegmentOffset(currentSrcPos);
                final long srcSegmentEnd = (currentSrcSegment + 1) * SEGMENT_SIZE;
                final long srcRemainingInSegment = Math.min(srcSegmentEnd - currentSrcPos, remainingLength);

                segments[currentSrcSegment].getBooleans(currentSrcOffset, dest, currentDestPos, srcRemainingInSegment);

                currentSrcPos += srcRemainingInSegment;
                currentDestPos += srcRemainingInSegment;
                remainingLength -= srcRemainingInSegment;
            }
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < segments.length; i++) {
            final RoaringLargeBitSet segment = segments[i];
            segment.clear();
        }
    }

    @Override
    public void clear(final long index, final long length) {
        final long endIndex = index + length;
        long currentIndex = index;

        while (currentIndex < endIndex) {
            final int segmentIndex = getSegmentIndex(currentIndex);
            final long segmentOffset = getSegmentOffset(currentIndex);
            final long segmentEnd = Math.min((segmentIndex + 1) * SEGMENT_SIZE, endIndex);
            final long clearLength = segmentEnd - currentIndex;

            segments[segmentIndex].clear(segmentOffset, clearLength);
            currentIndex = segmentEnd;
        }
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    private final class SegmentedSkippingIndexProvider implements ISkippingLargeIndexProvider {

        private int currentSegment = 0;
        private ISkippingLargeIndexProvider currentProvider;
        private long currentPosition = -1;

        private SegmentedSkippingIndexProvider() {
            advanceToNextSegment();
        }

        private void advanceToNextSegment() {
            while (currentSegment < segments.length) {
                currentProvider = segments[currentSegment].newSkippingIndexProvider();
                final long nextInSegment = currentProvider.next(0);
                if (nextInSegment != ISkippingLargeIndexProvider.END) {
                    currentPosition = currentSegment * SEGMENT_SIZE + nextInSegment;
                    return;
                }
                currentSegment++;
            }
            currentPosition = ISkippingLargeIndexProvider.END;
        }

        @Override
        public long next(final long nextCandidate) {
            if (currentPosition == nextCandidate) {
                return nextCandidate;
            }

            if (currentPosition == ISkippingLargeIndexProvider.END) {
                return ISkippingLargeIndexProvider.END;
            }

            if (nextCandidate < currentPosition) {
                return currentPosition;
            }

            // Need to find the next position >= nextCandidate
            final int targetSegment = getSegmentIndex(nextCandidate);
            final long targetOffset = getSegmentOffset(nextCandidate);

            // If we need to move to a later segment
            if (targetSegment > currentSegment) {
                currentSegment = targetSegment;
                advanceToNextSegment();
                if (currentPosition == ISkippingLargeIndexProvider.END) {
                    return ISkippingLargeIndexProvider.END;
                }
                if (currentPosition >= nextCandidate) {
                    return currentPosition;
                }
            }

            // Search within current segment
            if (currentSegment == targetSegment) {
                final long nextInSegment = currentProvider.next(targetOffset);
                if (nextInSegment != ISkippingLargeIndexProvider.END) {
                    currentPosition = currentSegment * SEGMENT_SIZE + nextInSegment;
                    return currentPosition;
                } else {
                    // Move to next segment
                    currentSegment++;
                    advanceToNextSegment();
                    return currentPosition;
                }
            }

            return currentPosition;
        }
    }
}
