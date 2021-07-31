package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

@NotThreadSafe
public class PlainBooleanArray implements IBooleanArray {

    private final boolean[] values;

    public PlainBooleanArray(final int size) {
        this.values = new boolean[size];
    }

    public PlainBooleanArray(final boolean[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final boolean value) {
        values[index] = value;
    }

    @Override
    public boolean get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IBooleanArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        return new PlainBooleanArray(ArrayUtils.subarray(values, startIndexInclusive, endIndexExclusive));
    }

    @Override
    public boolean[] asArray() {
        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
