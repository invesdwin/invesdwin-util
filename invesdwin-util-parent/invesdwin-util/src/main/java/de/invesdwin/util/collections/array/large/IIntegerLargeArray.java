package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IIntegerLargeArrayAccessor;

public interface IIntegerLargeArray extends ILargeArray, IIntegerLargeArrayAccessor {

    void set(long index, int value);

    IIntegerLargeArray slice(long fromIndex, long length);

    int[] asArray(long fromIndex, int length);

    int[] asArrayCopy(long fromIndex, int length);

    void getIntegers(long srcPos, IIntegerLargeArray dest, long destPos, long length);

}
