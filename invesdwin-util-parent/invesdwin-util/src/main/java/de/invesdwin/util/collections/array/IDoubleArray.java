package de.invesdwin.util.collections.array;

public interface IDoubleArray {

    void set(int index, double value);

    double get(int index);

    int size();

    IDoubleArray subarray(int startIndexInclusive, int endIndexExclusive);

    double[] asArray();

    static IDoubleArray newInstance(final int size) {
        //plain arrays are significantly faster than direct buffers
        return new PlainDoubleArray(size);
    }

    static IDoubleArray newInstance(final double[] values) {
        return new PlainDoubleArray(values);
    }

}
