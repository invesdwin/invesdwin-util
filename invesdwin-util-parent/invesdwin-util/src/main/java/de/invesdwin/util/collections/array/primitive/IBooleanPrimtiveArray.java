package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.IBooleanPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.bitset.BitSetBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.empty.EmptyBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapBooleanPrimitiveArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

public interface IBooleanPrimtiveArray extends IPrimitiveArray, IBooleanPrimitiveArrayAccessor {

    void set(int index, boolean value);

    IBooleanPrimtiveArray slice(int fromIndex, int length);

    boolean[] asArray();

    boolean[] asArray(int fromIndex, int length);

    boolean[] asArrayCopy();

    boolean[] asArrayCopy(int fromIndex, int length);

    void getBooleans(int srcPos, IBooleanPrimtiveArray dest, int destPos, int length);

    static IBooleanPrimtiveArray newInstance(final int size) {
        if (size == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new BitSetBooleanPrimitiveArray(ILockCollectionFactory.getInstance(false).newBitSet(size));
    }

    /**
     * Should use BitSet instead
     */
    @Deprecated
    static IBooleanPrimtiveArray newInstance(final boolean[] values) {
        if (values.length == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new HeapBooleanPrimitiveArray(values);
    }

    static IBooleanPrimtiveArray newInstance(final IPrimitiveBitSet values) {
        if (values.size() == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new BitSetBooleanPrimitiveArray(values);
    }

}
