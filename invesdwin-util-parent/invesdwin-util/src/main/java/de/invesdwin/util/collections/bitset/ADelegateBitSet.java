package de.invesdwin.util.collections.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public abstract class ADelegateBitSet implements IBitSet {

    public ADelegateBitSet() {}

    protected abstract IBitSet getDelegate();

    @Override
    public int getId() {
        return getDelegate().getId();
    }

    @Override
    public void add(final int index) {
        getDelegate().add(index);
    }

    @Override
    public void remove(final int index) {
        getDelegate().remove(index);
    }

    @Override
    public boolean contains(final int index) {
        return getDelegate().contains(index);
    }

    @Override
    public IBitSet optimize() {
        return getDelegate().optimize();
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        return getDelegate().and(others);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        return getDelegate().andRange(fromInclusive, toExclusive, others);
    }

    @Override
    public IBitSet or(final IBitSet... others) {
        return getDelegate().or(others);
    }

    @Override
    public IBitSet orRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        return getDelegate().orRange(fromInclusive, toExclusive, others);
    }

    @Override
    public IBitSet negate() {
        return getDelegate().negate();
    }

    @Override
    public IBitSet negateShallow() {
        return getDelegate().negateShallow();
    }

    @Override
    public int getTrueCount() {
        return getDelegate().getTrueCount();
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return getDelegate().newSkippingIndexProvider();
    }

    @Override
    public void getBooleans(final int srcPos, final IBitSet dest, final int destPos, final int length) {
        getDelegate().getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public IBitSet unwrap() {
        return getDelegate().unwrap();
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        return getDelegate().getBuffer(buffer);
    }

}
