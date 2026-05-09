package de.invesdwin.util.collections.array.large.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class EmptyLongLargeArray implements ILongLargeArray {

    public static final EmptyLongLargeArray INSTANCE = new EmptyLongLargeArray();

    private EmptyLongLargeArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final long index, final long value) {}

    @Override
    public long get(final long index) {
        return Longs.DEFAULT_MISSING_VALUE;
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
    public ILongLargeArray slice(final long fromIndex, final long length) {
        return this;
    }

    @Override
    public long[] asArray(final long fromIndex, final int length) {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public long[] asArrayCopy(final long fromIndex, final int length) {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public void getLongs(final long srcPos, final ILongLargeArray dest, final long destPos, final long length) {
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
