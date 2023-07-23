package de.invesdwin.util.collections.array;

public interface IIntegerArray {

    void set(int index, int value);

    int get(int index);

    int size();

    IIntegerArray slice(int fromIndex, int length);

    int[] asArray();

    int[] asArray(int fromIndex, int length);

    int[] asArrayCopy();

    int[] asArrayCopy(int fromIndex, int length);

    void getIntegers(int srcPos, IIntegerArray dest, int destPos, int length);

    static IIntegerArray newInstance(final int size) {
        if (size == 0) {
            return EmptyIntegerArray.INSTANCE;
        }
        return new HeapIntegerArray(size);
    }

    static IIntegerArray newInstance(final int[] values) {
        if (values.length == 0) {
            return EmptyIntegerArray.INSTANCE;
        }
        return new HeapIntegerArray(values);
    }

}
