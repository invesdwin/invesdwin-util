package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyBooleanArray implements IBooleanArray {

    public static final EmptyBooleanArray INSTANCE = new EmptyBooleanArray();

    private EmptyBooleanArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final int index, final boolean value) {}

    @Override
    public boolean get(final int index) {
        return Booleans.DEFAULT_MISSING_VALUE;
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
    public IBooleanArray slice(final int fromIndex, final int length) {
        return this;
    }

    @Override
    public boolean[] asArray() {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public boolean[] asArrayCopy() {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        return Booleans.EMPTY_ARRAY;
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
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
