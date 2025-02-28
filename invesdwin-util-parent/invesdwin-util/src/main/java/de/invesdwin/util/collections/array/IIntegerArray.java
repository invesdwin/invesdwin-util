package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.accessor.IIntegerArrayAccessor;
import de.invesdwin.util.collections.array.heap.HeapIntegerArray;

public interface IIntegerArray extends IPrimitiveArray, IIntegerArrayAccessor {

    void set(int index, int value);

    IIntegerArray slice(int fromIndex, int length);

    int[] asArray();

    int[] asArray(int fromIndex, int length);

    int[] asArrayCopy();

    int[] asArrayCopy(int fromIndex, int length);

    void getIntegers(int srcPos, IIntegerArray dest, int destPos, int length);

    static IIntegerArray newInstance(final int size) {
        if (size == 0) {
            return EmptyIntegerArray.INSTANCE;
        }
        return new HeapIntegerArray(size);
    }

    static IIntegerArray newInstance(final int[] values) {
        if (values.length == 0) {
            return EmptyIntegerArray.INSTANCE;
        }
        return new HeapIntegerArray(values);
    }

}
