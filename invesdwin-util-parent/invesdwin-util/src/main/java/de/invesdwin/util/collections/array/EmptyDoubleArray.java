package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;

@Immutable
public final class EmptyDoubleArray implements IDoubleArray {

    public static final EmptyDoubleArray INSTANCE = new EmptyDoubleArray();

    private EmptyDoubleArray() {
    }

    @Override
    public void set(final int index, final double value) {
    }

    @Override
    public double get(final int index) {
        return Double.NaN;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public IDoubleArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        return this;
    }

    @Override
    public double[] asArray() {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public String toString() {
        return "[]";
    }

}