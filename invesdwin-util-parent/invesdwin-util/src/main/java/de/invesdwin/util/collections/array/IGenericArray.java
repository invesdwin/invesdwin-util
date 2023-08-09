package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.heap.HeapGenericArray;

public interface IGenericArray<E> extends IPrimitiveArray {

    void set(int index, E value);

    E get(int index);

    IGenericArray<E> slice(int fromIndex, int length);

    E[] asArray();

    E[] asArray(int fromIndex, int length);

    E[] asArrayCopy();

    E[] asArrayCopy(int fromIndex, int length);

    void getGenerics(int srcPos, IGenericArray<E> dest, int destPos, int length);

    static <T> IGenericArray<T> newInstance(final int size) {
        if (size == 0) {
            return EmptyGenericArray.getInstance();
        }
        return new HeapGenericArray<T>(size);
    }

    static <T> IGenericArray<T> newInstance(final T[] values) {
        if (values.length == 0) {
            return EmptyGenericArray.getInstance();
        }
        return new HeapGenericArray<T>(values);
    }

}
