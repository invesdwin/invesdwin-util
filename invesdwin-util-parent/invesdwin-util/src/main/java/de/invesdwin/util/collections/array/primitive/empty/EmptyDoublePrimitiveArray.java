package de.invesdwin.util.collections.array.primitive.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyDoublePrimitiveArray implements IDoublePrimitiveArray {

    public static final EmptyDoublePrimitiveArray INSTANCE = new EmptyDoublePrimitiveArray();

    private EmptyDoublePrimitiveArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final int index, final double value) {}

    @Override
    public double get(final int index) {
        return Double.NaN;
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
    public IDoublePrimitiveArray slice(final int fromIndex, final int length) {
        return this;
    }

    @Override
    public double[] asArray() {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public double[] asArray(final int fromIndex, final int length) {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public double[] asArrayCopy() {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public double[] asArrayCopy(final int fromIndex, final int length) {
        return Doubles.EMPTY_ARRAY;
    }

    @Override
    public void getDoubles(final int srcPos, final IDoublePrimitiveArray dest, final int destPos, final int length) {
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
