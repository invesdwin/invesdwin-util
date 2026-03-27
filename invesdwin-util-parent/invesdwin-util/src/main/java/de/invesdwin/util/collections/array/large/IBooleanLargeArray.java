package de.invesdwin.util.collections.array.large;

import de.invesdwin.util.collections.array.large.accessor.IBooleanLargeArrayAccessor;

public interface IBooleanLargeArray extends ILargeArray, IBooleanLargeArrayAccessor {

    void set(long index, boolean value);

    IBooleanLargeArray slice(long fromIndex, long length);

    boolean[] asArray(long fromIndex, int length);

    boolean[] asArrayCopy(long fromIndex, int length);

    void getBooleans(long srcPos, IBooleanLargeArray dest, long destPos, long length);

}
