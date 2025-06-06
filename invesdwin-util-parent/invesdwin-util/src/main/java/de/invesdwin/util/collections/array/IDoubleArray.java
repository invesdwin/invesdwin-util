package de.invesdwin.util.collections.array;

import de.invesdwin.util.collections.array.accessor.IDoubleArrayAccessor;
import de.invesdwin.util.collections.array.heap.HeapDoubleArray;

public interface IDoubleArray extends IPrimitiveArray, IDoubleArrayAccessor {

    void set(int index, double value);

    IDoubleArray slice(int fromIndex, int length);

    double[] asArray();

    double[] asArray(int fromIndex, int length);

    double[] asArrayCopy();

    double[] asArrayCopy(int fromIndex, int length);

    void getDoubles(int srcPos, IDoubleArray dest, int destPos, int length);

    static IDoubleArray newInstance(final int size) {
        if (size == 0) {
            return EmptyDoubleArray.INSTANCE;
        }
        //plain arrays are significantly faster than direct buffers
        return new HeapDoubleArray(size);
    }

    static IDoubleArray newInstance(final double[] values) {
        if (values.length == 0) {
            return EmptyDoubleArray.INSTANCE;
        }
        return new HeapDoubleArray(values);
    }

}
