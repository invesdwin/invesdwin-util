package de.invesdwin.util.collections.bitset;

import java.io.IOException;

import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IBitSet extends IPrimitiveArray {

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

    boolean isEmpty();

    ISkippingIndexProvider newSkippingIndexProvider();

    IBitSet unwrap();

    void getBooleans(int srcPos, IBitSet dest, int destPos, int length);

    @Override
    int getBuffer(IByteBuffer buffer) throws IOException;

}
