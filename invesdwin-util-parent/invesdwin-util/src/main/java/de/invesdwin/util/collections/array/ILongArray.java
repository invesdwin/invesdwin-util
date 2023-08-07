package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.heap.HeapLongArray;

public interface ILongArray extends IPrimitiveArray {

    void set(int index, long value);

    long get(int index);

    ILongArray slice(int fromIndex, int length);

    long[] asArray();

    long[] asArray(int fromIndex, int length);

    long[] asArrayCopy();

    long[] asArrayCopy(int fromIndex, int length);

    void getLongs(int srcPos, ILongArray dest, int destPos, int length);

    static ILongArray newInstance(final int size) {
        if (size == 0) {
            return EmptyLongArray.INSTANCE;
        }
        return new HeapLongArray(size);
    }

    static ILongArray newInstance(final long[] values) {
        if (values.length == 0) {
            return EmptyLongArray.INSTANCE;
        }
        return new HeapLongArray(values);
    }

}
