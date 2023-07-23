package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.bitset.IBitSet;

public interface IBooleanArray {

    void set(int index, boolean value);

    boolean get(int index);

    int size();

    IBooleanArray slice(int fromIndex, int length);

    boolean[] asArray();

    boolean[] asArray(int fromIndex, int length);

    static IBooleanArray newInstance(final int size) {
        if (size == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new HeapBooleanArray(size);
    }

    static IBooleanArray newInstance(final boolean[] values) {
        if (values.length == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new HeapBooleanArray(values);
    }

    static IBooleanArray newInstance(final IBitSet values) {
        if (values.getExpectedSize() == 0) {
            return EmptyBooleanArray.INSTANCE;
        }
        return new BitSetBooleanArray(values);
    }

}
