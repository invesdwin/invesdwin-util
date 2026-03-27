package de.invesdwin.util.collections.array.large.bitset;

import java.io.IOException;

import de.invesdwin.util.collections.array.large.ILargeArray;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface ILargeBitSet extends ILargeArray {

    void add(long index);

    void remove(long index);

    boolean contains(long index);

    void flip(long index);

    void flip(long index, long length);

    ILargeBitSet optimize();

    ILargeBitSet and(ILargeBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    ILargeBitSet andRange(long fromInclusive, long toExclusive, ILargeBitSet... others);

    ILargeBitSet or(ILargeBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    ILargeBitSet orRange(long fromInclusive, long toExclusive, ILargeBitSet... others);

    /**
     * This creates a negated copy of the underlying bitset.
     */
    ILargeBitSet negate();

    /**
     * This creates a wrapper around the underlying bitset that negates its values.
     */
    ILargeBitSet negateShallow();

    long getTrueCount();

    @Override
    boolean isEmpty();

    ISkippingLargeIndexProvider newSkippingIndexProvider();

    ILargeBitSet unwrap();

    void getBooleans(long srcPos, ILargeBitSet dest, long destPos, long length);

    void clear(long index, long length);

    @Override
    long getBuffer(IMemoryBuffer buffer) throws IOException;

}
