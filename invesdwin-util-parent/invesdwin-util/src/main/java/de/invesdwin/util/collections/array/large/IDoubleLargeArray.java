package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IDoubleLargeArrayAccessor;

public interface IDoubleLargeArray extends ILargeArray, IDoubleLargeArrayAccessor {

    void set(long index, double value);

    IDoubleLargeArray slice(long fromIndex, long length);

    double[] asArray(long fromIndex, int length);

    double[] asArrayCopy(long fromIndex, int length);

    void getDoubles(long srcPos, IDoubleLargeArray dest, long destPos, long length);

}
