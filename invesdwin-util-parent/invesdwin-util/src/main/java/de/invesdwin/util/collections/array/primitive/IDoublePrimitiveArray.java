package de.invesdwin.util.collections.array.primitive;

import de.invesdwin.util.collections.array.primitive.accessor.IDoublePrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.empty.EmptyDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.heap.HeapDoublePrimitiveArray;

public interface IDoublePrimitiveArray extends IPrimitiveArray, IDoublePrimitiveArrayAccessor {

    void set(int index, double value);

    IDoublePrimitiveArray slice(int fromIndex, int length);

    double[] asArray();

    double[] asArray(int fromIndex, int length);

    double[] asArrayCopy();

    double[] asArrayCopy(int fromIndex, int length);

    void getDoubles(int srcPos, IDoublePrimitiveArray dest, int destPos, int length);

    static IDoublePrimitiveArray newInstance(final int size) {
        if (size == 0) {
            return EmptyDoublePrimitiveArray.INSTANCE;
        }
        //plain arrays are significantly faster than direct buffers
        return new HeapDoublePrimitiveArray(size);
    }

    static IDoublePrimitiveArray newInstance(final double[] values) {
        if (values.length == 0) {
            return EmptyDoublePrimitiveArray.INSTANCE;
        }
        return new HeapDoublePrimitiveArray(values);
    }

}
