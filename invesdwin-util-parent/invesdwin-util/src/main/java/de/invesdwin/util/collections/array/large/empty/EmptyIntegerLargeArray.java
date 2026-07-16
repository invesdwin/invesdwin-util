package de.invesdwin.util.collections.array.large.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class EmptyIntegerLargeArray implements IIntegerLargeArray {

    public static final EmptyIntegerLargeArray INSTANCE = new EmptyIntegerLargeArray();

    private EmptyIntegerLargeArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final long index, final int value) {}

    @Override
    public int get(final long index) {
        return Integers.DEFAULT_MISSING_VALUE;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public IIntegerLargeArray slice(final long fromIndex, final long length) {
        return this;
    }

    @Override
    public int[] asArray(final long fromIndex, final int length) {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public int[] asArrayCopy(final long fromIndex, final int length) {
        return Integers.EMPTY_ARRAY;
    }

    @Override
    public void getIntegers(final long srcPos, final IIntegerLargeArray dest, final long destPos, final long length) {
        //noop
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        return 0;
    }

    @Override
    public long getBufferLength() {
        return 0;
    }

    @Override
    public void clear() {}

}
