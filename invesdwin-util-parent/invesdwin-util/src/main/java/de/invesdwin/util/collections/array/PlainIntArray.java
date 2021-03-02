package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PlainIntArray implements IIntArray {

    private final int[] values;

    public PlainIntArray(final int size) {
        this.values = new int[size];
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

}
