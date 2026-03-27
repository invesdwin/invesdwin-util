package de.invesdwin.util.collections.array.large.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class EmptyDoubleLargeArray implements IDoubleLargeArray {

    public static final EmptyDoubleLargeArray INSTANCE = new EmptyDoubleLargeArray();

    private EmptyDoubleLargeArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final long index, final double value) {}

    @Override
    public double get(final long index) {
        return Double.NaN;
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
    public IDoubleLargeArray slice(final long fromIndex, final long length) {
        return this;
    }

    @Override
    public double[] asArray(final long fromIndex, final int length) {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public double[] asArrayCopy(final long fromIndex, final int length) {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public void getDoubles(final long srcPos, final IDoubleLargeArray dest, final long destPos, final long length) {
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
