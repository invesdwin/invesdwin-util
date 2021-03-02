package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PlainDoubleArray implements IDoubleArray {

    private final double[] values;

    public PlainDoubleArray(final int size) {
        this.values = new double[size];
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

}
