package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Longs;

@Immutable
public final class EmptyLongArray implements ILongArray {

    public static final EmptyLongArray INSTANCE = new EmptyLongArray();

    private EmptyLongArray() {}

    @Override
    public void set(final int index, final long value) {}

    @Override
    public long get(final int index) {
        return Longs.DEFAULT_MISSING_VALUE;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ILongArray slice(final int fromIndex, final int length) {
        return this;
    }

    @Override
    public long[] asArray() {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public long[] asArray(final int fromIndex, final int length) {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public void arrayCopy(final int srcPos, final ILongArray dest, final int destPos, final int length) {}

}
