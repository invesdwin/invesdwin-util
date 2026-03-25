package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.IIntegerPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.empty.EmptyIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapIntegerPrimitiveArray;

public interface IIntegerPrimitiveArray extends IPrimitiveArray, IIntegerPrimitiveArrayAccessor {

    void set(int index, int value);

    IIntegerPrimitiveArray slice(int fromIndex, int length);

    int[] asArray();

    int[] asArray(int fromIndex, int length);

    int[] asArrayCopy();

    int[] asArrayCopy(int fromIndex, int length);

    void getIntegers(int srcPos, IIntegerPrimitiveArray dest, int destPos, int length);

    static IIntegerPrimitiveArray newInstance(final int size) {
        if (size == 0) {
            return EmptyIntegerPrimitiveArray.INSTANCE;
        }
        return new HeapIntegerPrimitiveArray(size);
    }

    static IIntegerPrimitiveArray newInstance(final int[] values) {
        if (values.length == 0) {
            return EmptyIntegerPrimitiveArray.INSTANCE;
        }
        return new HeapIntegerPrimitiveArray(values);
    }

}
