package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.heap.HeapBooleanArray;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

public interface IBooleanArray extends IPrimitiveArray {

    void set(int index, boolean value);

    boolean get(int index);

    IBooleanArray slice(int fromIndex, int length);

    boolean[] asArray();

    boolean[] asArray(int fromIndex, int length);

    boolean[] asArrayCopy();

    boolean[] asArrayCopy(int fromIndex, int length);

    void getBooleans(int srcPos, IBooleanArray dest, int destPos, int length);

    static IBooleanArray newInstance(final int size) {
        if (size == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new BitSetBooleanArray(ILockCollectionFactory.getInstance(false).newBitSet(size));
    }

    /**
     * Should use BitSet instead
     */
    @Deprecated
    static IBooleanArray newInstance(final boolean[] values) {
        if (values.length == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new HeapBooleanArray(values);
    }

    static IBooleanArray newInstance(final IBitSet values) {
        if (values.size() == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new BitSetBooleanArray(values);
    }

}
