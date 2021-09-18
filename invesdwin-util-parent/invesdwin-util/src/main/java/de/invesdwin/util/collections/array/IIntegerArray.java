package de.invesdwin.util.collections.array;

public interface IIntegerArray {

    void set(int index, int value);

    int get(int index);

    int size();

    IIntegerArray slice(int fromIndex, int length);

    int[] asArray();

    int[] asArray(int fromIndex, int length);

    static IIntegerArray newInstance(final int size) {
        return new HeapIntegerArray(size);
    }

    static IIntegerArray newInstance(final int[] values) {
        return new HeapIntegerArray(values);
    }

}
