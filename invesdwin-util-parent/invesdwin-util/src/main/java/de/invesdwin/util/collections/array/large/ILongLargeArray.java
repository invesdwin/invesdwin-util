package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.ILongLargeArrayAccessor;

public interface ILongLargeArray extends ILargeArray, ILongLargeArrayAccessor {

    void set(long index, long value);

    ILongLargeArray slice(long fromIndex, long length);

    long[] asArray(long fromIndex, int length);

    long[] asArrayCopy(long fromIndex, int length);

    void getLongs(long srcPos, ILongLargeArray dest, long destPos, long length);

}
