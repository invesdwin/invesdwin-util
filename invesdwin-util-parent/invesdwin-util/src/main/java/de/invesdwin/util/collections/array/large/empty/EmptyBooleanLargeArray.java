package de.invesdwin.util.collections.array.large.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class EmptyBooleanLargeArray implements IBooleanLargeArray {

    public static final EmptyBooleanLargeArray INSTANCE = new EmptyBooleanLargeArray();

    private EmptyBooleanLargeArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final long index, final boolean value) {}

    @Override
    public boolean get(final long index) {
        return Booleans.DEFAULT_MISSING_VALUE;
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
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return this;
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
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
