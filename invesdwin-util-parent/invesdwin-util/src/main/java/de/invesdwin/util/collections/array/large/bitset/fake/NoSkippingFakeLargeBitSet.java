package de.invesdwin.util.collections.array.large.bitset.fake;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class NoSkippingFakeLargeBitSet implements ILargeBitSet {

    public static final NoSkippingFakeLargeBitSet INSTANCE = new NoSkippingFakeLargeBitSet();

    private NoSkippingFakeLargeBitSet() {}

    @Override
    public int getId() {
        return ID_DISABLED;
    }

    @Override
    public void remove(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet optimize() {
        return this;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getTrueCount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flip(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flip(final long index, final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet negateShallow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILargeBitSet unwrap() {
        return this;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(final long index, final long length) {
        throw new UnsupportedOperationException();
    }
}