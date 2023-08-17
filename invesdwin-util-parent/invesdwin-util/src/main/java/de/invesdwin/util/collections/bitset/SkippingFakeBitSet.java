package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class SkippingFakeBitSet implements IBitSet {

    private final ISkippingIndexProvider skippingIndexProvider;

    public SkippingFakeBitSet(final ISkippingIndexProvider skippingIndexProvider) {
        this.skippingIndexProvider = skippingIndexProvider;
    }

    @Override
    public void remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet optimize() {
        return this;
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return skippingIndexProvider;
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
    public int getExpectedSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet negateShallow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }
}