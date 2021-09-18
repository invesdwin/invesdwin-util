package de.invesdwin.util.collections.array;

public interface IBooleanArray {

    void set(int index, boolean value);

    boolean get(int index);

    int size();

    IBooleanArray slice(int fromIndex, int length);

    boolean[] asArray();

    boolean[] asArray(int fromIndex, int length);

    static IBooleanArray newInstance(final int size) {
        return new HeapBooleanArray(size);
    }

    static IBooleanArray newInstance(final boolean[] values) {
        return new HeapBooleanArray(values);
    }

}
