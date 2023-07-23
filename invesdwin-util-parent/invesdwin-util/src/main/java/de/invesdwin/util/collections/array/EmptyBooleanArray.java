package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;

@Immutable
public final class EmptyBooleanArray implements IBooleanArray {

    public static final EmptyBooleanArray INSTANCE = new EmptyBooleanArray();

    private EmptyBooleanArray() {}

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

}
