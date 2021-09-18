package de.invesdwin.util.collections.array;

public interface IDoubleArray {

    void set(int index, double value);

    double get(int index);

    int size();

    IDoubleArray slice(int fromIndex, int length);

    double[] asArray();

    double[] asArray(int fromIndex, int length);

    static IDoubleArray newInstance(final int size) {
        //plain arrays are significantly faster than direct buffers
        return new HeapDoubleArray(size);
    }

    static IDoubleArray newInstance(final double[] values) {
        return new HeapDoubleArray(values);
    }

}
