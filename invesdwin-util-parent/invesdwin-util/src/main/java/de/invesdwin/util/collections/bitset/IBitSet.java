package de.invesdwin.util.collections.bitset;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IBitSet {

    void add(int index);

    void remove(int index);

    boolean contains(int index);

    IBitSet optimize();

    IBitSet and(IBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    IBitSet andRange(int fromInclusive, int toExclusive, IBitSet[] others);

    IBitSet or(IBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    IBitSet orRange(int fromInclusive, int toExclusive, IBitSet[] others);

    /**
     * This creates a negated copy of the underlying bitset.
     */
    IBitSet negate();

    /**
     * This creates a wrapper around the underlying bitset that negates its values.
     */
    IBitSet negateShallow();

    int getTrueCount();

    int getExpectedSize();

    boolean isEmpty();

    ISkippingIndexProvider newSkippingIndexProvider();

    IBitSet unwrap();

    void getBooleans(int srcPos, IBitSet dest, int destPos, int length);

    int toBuffer(IByteBuffer buffer);

}
