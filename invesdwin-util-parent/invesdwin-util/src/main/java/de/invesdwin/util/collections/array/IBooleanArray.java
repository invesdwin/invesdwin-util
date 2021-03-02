package de.invesdwin.util.collections.array;

public interface IBooleanArray {

    void set(int index, boolean value);

    boolean get(int index);

    int size();

    IBooleanArray subarray(int startIndexInclusive, int endIndexExclusive);

    boolean[] asArray();

    static IBooleanArray newInstance(final int size) {
        return new PlainBooleanArray(size);
    }

    static IBooleanArray newInstance(final boolean[] values) {
        return new PlainBooleanArray(values);
    }

}
