package de.invesdwin.util.collections.array.primitive.bitset.fake;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class NoSkippingFakePrimitiveBitSet implements IPrimitiveBitSet {

    public static final NoSkippingFakePrimitiveBitSet INSTANCE = new NoSkippingFakePrimitiveBitSet();

    private NoSkippingFakePrimitiveBitSet() {}

    @Override
    public int getId() {
        return ID_DISABLED;
    }

    @Override
    public void remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet optimize() {
        return this;
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getTrueCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}