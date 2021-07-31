package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

@NotThreadSafe
public class PlainIntegerArray implements IIntegerArray {

    private final int[] values;

    public PlainIntegerArray(final int size) {
        this.values = new int[size];
    }

    public PlainIntegerArray(final int[] values) {
        this.values = values;
    }

    @Override
    public void set(final int index, final int value) {
        values[index] = value;
    }

    @Override
    public int get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public IIntegerArray subarray(final int startIndexInclusive, final int endIndexExclusive) {
        return new PlainIntegerArray(ArrayUtils.subarray(values, startIndexInclusive, endIndexExclusive));
    }

    @Override
    public int[] asArray() {
        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
