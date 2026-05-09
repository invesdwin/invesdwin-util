package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.IBooleanPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.bitset.BitSetBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.empty.EmptyBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapBooleanPrimitiveArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

public interface IBooleanPrimitiveArray extends IPrimitiveArray, IBooleanPrimitiveArrayAccessor {

    void set(int index, boolean value);

    IBooleanPrimitiveArray slice(int fromIndex, int length);

    boolean[] asArray();

    boolean[] asArray(int fromIndex, int length);

    boolean[] asArrayCopy();

    boolean[] asArrayCopy(int fromIndex, int length);

    void getBooleans(int srcPos, IBooleanPrimitiveArray dest, int destPos, int length);

    static IBooleanPrimitiveArray newInstance(final int size) {
        if (size == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new BitSetBooleanPrimitiveArray(ILockCollectionFactory.getInstance(false).newPrimitiveBitSet(size));
    }

    /**
     * Should use BitSet instead
     */
    @Deprecated
    static IBooleanPrimitiveArray newInstance(final boolean[] values) {
        if (values.length == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new HeapBooleanPrimitiveArray(values);
    }

    static IBooleanPrimitiveArray newInstance(final IPrimitiveBitSet values) {
        if (values.size() == 0) {
            return EmptyBooleanPrimitiveArray.INSTANCE;
        }
        return new BitSetBooleanPrimitiveArray(values);
    }

}
