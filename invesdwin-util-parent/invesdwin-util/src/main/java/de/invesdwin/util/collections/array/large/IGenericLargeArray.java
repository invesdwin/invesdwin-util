package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IGenericLargeArrayAccessor;

public interface IGenericLargeArray<E> extends ILargeArray, IGenericLargeArrayAccessor<E> {

    void set(long index, E value);

    IGenericLargeArray<E> slice(long fromIndex, long length);

    E[] asArray(long fromIndex, int length);

    E[] asArrayCopy(long fromIndex, int length);

    void getGenerics(long srcPos, IGenericLargeArray<E> dest, long destPos, long length);

}
