package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

@NotThreadSafe
public class PlainDoubleArray implements IDoubleArray {

    private final double[] values;

    public PlainDoubleArray(final int size) {
        this.values = new double[size];
    }

    public PlainDoubleArray(final double[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final double value) {
        values[index] = value;
    }

    @Override
    public double get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IDoubleArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        return new PlainDoubleArray(ArrayUtils.subarray(values, startIndexInclusive, endIndexExclusive));
    }

    @Override
    public double[] asArray() {
        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
