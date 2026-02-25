package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyIntegerArray implements IIntegerArray {

    public static final EmptyIntegerArray INSTANCE = new EmptyIntegerArray();

    private EmptyIntegerArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final int index, final int value) {}

    @Override
    public int get(final int index) {
        return Integers.DEFAULT_MISSING_VALUE;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public IIntegerArray slice(final int fromIndex, final int length) {
        return this;
    }

    @Override
    public int[] asArray() {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public int[] asArray(final int fromIndex, final int length) {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public int[] asArrayCopy() {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public int[] asArrayCopy(final int fromIndex, final int length) {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public void getIntegers(final int srcPos, final IIntegerArray dest, final int destPos, final int length) {
        //noop
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        return 0;
    }

    @Override
    public void clear() {}

}
