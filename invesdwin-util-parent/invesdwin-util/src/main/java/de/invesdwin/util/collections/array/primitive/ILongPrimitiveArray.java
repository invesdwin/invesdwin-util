package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.ILongPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.empty.EmptyLongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapLongPrimitiveArray;

public interface ILongPrimitiveArray extends IPrimitiveArray, ILongPrimitiveArrayAccessor {

    void set(int index, long value);

    ILongPrimitiveArray slice(int fromIndex, int length);

    long[] asArray();

    long[] asArray(int fromIndex, int length);

    long[] asArrayCopy();

    long[] asArrayCopy(int fromIndex, int length);

    void getLongs(int srcPos, ILongPrimitiveArray dest, int destPos, int length);

    static ILongPrimitiveArray newInstance(final int size) {
        if (size == 0) {
            return EmptyLongPrimitiveArray.INSTANCE;
        }
        return new HeapLongPrimitiveArray(size);
    }

    static ILongPrimitiveArray newInstance(final long[] values) {
        if (values.length == 0) {
            return EmptyLongPrimitiveArray.INSTANCE;
        }
        return new HeapLongPrimitiveArray(values);
    }

}
