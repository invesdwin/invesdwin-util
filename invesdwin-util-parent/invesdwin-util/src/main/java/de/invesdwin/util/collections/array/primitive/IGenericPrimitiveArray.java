package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.IGenericPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.empty.EmptyGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapGenericPrimitiveArray;

public interface IGenericPrimitiveArray<E> extends IPrimitiveArray, IGenericPrimitiveArrayAccessor<E> {

    void set(int index, E value);

    IGenericPrimitiveArray<E> slice(int fromIndex, int length);

    E[] asArray();

    E[] asArray(int fromIndex, int length);

    E[] asArrayCopy();

    E[] asArrayCopy(int fromIndex, int length);

    void getGenerics(int srcPos, IGenericPrimitiveArray<E> dest, int destPos, int length);

    static <T> IGenericPrimitiveArray<T> newInstance(final Class<T> type, final int size) {
        if (size == 0) {
            return EmptyGenericPrimitiveArray.getInstance();
        }
        return new HeapGenericPrimitiveArray<T>(type, size);
    }

    static <T> IGenericPrimitiveArray<T> newInstance(final T[] emptyArray, final int size) {
        if (size == 0) {
            return EmptyGenericPrimitiveArray.getInstance();
        }
        return new HeapGenericPrimitiveArray<T>(emptyArray, size);
    }

    static <T> IGenericPrimitiveArray<T> newInstance(final T[] values) {
        if (values.length == 0) {
            return EmptyGenericPrimitiveArray.getInstance();
        }
        return new HeapGenericPrimitiveArray<T>(values);
    }

}
