package de.invesdwin.util.collections.array.primitive.bitset;

import java.io.IOException;

import de.invesdwin.util.collections.array.primitive.IPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveBitSet extends IPrimitiveArray {

    void add(int index);

    void remove(int index);

    boolean contains(int index);

    void flip(int index);

    void flip(int index, int length);

    IPrimitiveBitSet optimize();

    IPrimitiveBitSet and(IPrimitiveBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    IPrimitiveBitSet andRange(int fromInclusive, int toExclusive, IPrimitiveBitSet... others);

    IPrimitiveBitSet or(IPrimitiveBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    IPrimitiveBitSet orRange(int fromInclusive, int toExclusive, IPrimitiveBitSet... others);

    /**
     * This creates a negated copy of the underlying bitset.
     */
    IPrimitiveBitSet negate();

    /**
     * This creates a wrapper around the underlying bitset that negates its values.
     */
    IPrimitiveBitSet negateShallow();

    int getTrueCount();

    @Override
    boolean isEmpty();

    ISkippingPrimitiveIndexProvider newSkippingIndexProvider();

    IPrimitiveBitSet unwrap();

    void getBooleans(int srcPos, IPrimitiveBitSet dest, int destPos, int length);

    void clear(int index, int length);

    @Override
    int getBuffer(IByteBuffer buffer) throws IOException;

}
