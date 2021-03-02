package de.invesdwin.util.collections.array;

public interface IIntegerArray {

    void set(int index, int value);

    int get(int index);

    int size();

    IIntegerArray subarray(int startIndexInclusive, int endIndexExclusive);

    int[] asArray();

    static IIntegerArray newInstance(final int size) {
        return new PlainIntegerArray(size);
    }

    static IIntegerArray newInstance(final int[] values) {
        return new PlainIntegerArray(values);
    }

}
