package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.accessor.IGenericArrayAccessor;
import de.invesdwin.util.collections.array.heap.HeapGenericArray;

public interface IGenericArray<E> extends IPrimitiveArray, IGenericArrayAccessor<E> {

    void set(int index, E value);

    IGenericArray<E> slice(int fromIndex, int length);

    E[] asArray();

    E[] asArray(int fromIndex, int length);

    E[] asArrayCopy();

    E[] asArrayCopy(int fromIndex, int length);

    void getGenerics(int srcPos, IGenericArray<E> dest, int destPos, int length);

    static <T> IGenericArray<T> newInstance(final Class<T> type, final int size) {
        if (size == 0) {
            return EmptyGenericArray.getInstance();
        }
        return new HeapGenericArray<T>(type, size);
    }

    static <T> IGenericArray<T> newInstance(final T[] emptyArray, final int size) {
        if (size == 0) {
            return EmptyGenericArray.getInstance();
        }
        return new HeapGenericArray<T>(emptyArray, size);
    }

    static <T> IGenericArray<T> newInstance(final T[] values) {
        if (values.length == 0) {
            return EmptyGenericArray.getInstance();
        }
        return new HeapGenericArray<T>(values);
    }

}
