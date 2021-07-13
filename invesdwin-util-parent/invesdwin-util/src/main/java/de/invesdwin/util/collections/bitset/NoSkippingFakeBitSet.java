package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class NoSkippingFakeBitSet implements IBitSet {

    public static final NoSkippingFakeBitSet INSTANCE = new NoSkippingFakeBitSet();

    private NoSkippingFakeBitSet() {
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
    public IBitSet unwrap() {
        return this;
    }
}