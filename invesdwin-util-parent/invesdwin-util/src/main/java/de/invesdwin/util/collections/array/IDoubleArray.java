package de.invesdwin.util.collections.array;

public interface IDoubleArray {

    void set(int index, double value);

    double get(int index);

    int size();

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
