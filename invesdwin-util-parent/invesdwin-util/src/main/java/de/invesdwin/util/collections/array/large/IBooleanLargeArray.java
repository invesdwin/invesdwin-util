package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IBooleanLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.bitset.BitSetBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.empty.EmptyBooleanLargeArray;
import de.invesdwin.util.collections.array.large.heap.HeapBooleanLargeArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

public interface IBooleanLargeArray extends ILargeArray, IBooleanLargeArrayAccessor {

    void set(long index, boolean value);

    IBooleanLargeArray slice(long fromIndex, long length);

    boolean[] asArray(long fromIndex, int length);

    boolean[] asArrayCopy(long fromIndex, int length);

    void getBooleans(long srcPos, IBooleanLargeArray dest, long destPos, long length);

    static IBooleanLargeArray newInstance(final int size) {
        if (size == 0) {
            return EmptyBooleanLargeArray.INSTANCE;
        }
        return new BitSetBooleanLargeArray(ILockCollectionFactory.getInstance(false).newLargeBitSet(size));
    }

    /**
     * Should use BitSet instead
     */
    @Deprecated
    static IBooleanLargeArray newInstance(final boolean[] values) {
        if (values.length == 0) {
            return EmptyBooleanLargeArray.INSTANCE;
        }
        return new HeapBooleanLargeArray(values);
    }

    static IBooleanLargeArray newInstance(final ILargeBitSet values) {
        if (values.size() == 0) {
            return EmptyBooleanLargeArray.INSTANCE;
        }
        return new BitSetBooleanLargeArray(values);
    }

}
