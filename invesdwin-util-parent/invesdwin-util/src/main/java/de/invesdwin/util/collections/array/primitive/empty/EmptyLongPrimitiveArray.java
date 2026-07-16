package de.invesdwin.util.collections.array.primitive.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyLongPrimitiveArray implements ILongPrimitiveArray {

    public static final EmptyLongPrimitiveArray INSTANCE = new EmptyLongPrimitiveArray();

    private EmptyLongPrimitiveArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

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
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ILongPrimitiveArray slice(final int fromIndex, final int length) {
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
    public long[] asArrayCopy() {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public long[] asArrayCopy(final int fromIndex, final int length) {
        return Longs.EMPTY_ARRAY;
    }

    @Override
    public void getLongs(final int srcPos, final ILongPrimitiveArray dest, final int destPos, final int length) {
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
    public int getBufferLength() {
        return 0;
    }

    @Override
    public void clear() {}

}
