package de.invesdwin.util.collections.array.large.bitset.delegate;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public abstract class ADelegateLargeBitSet implements ILargeBitSet {

    public ADelegateLargeBitSet() {}

    protected abstract ILargeBitSet getDelegate();

    @Override
    public int getId() {
        return getDelegate().getId();
    }

    @Override
    public void add(final long index) {
        getDelegate().add(index);
    }

    @Override
    public void remove(final long index) {
        getDelegate().remove(index);
    }

    @Override
    public boolean contains(final long index) {
        return getDelegate().contains(index);
    }

    @Override
    public void flip(final long index) {
        getDelegate().flip(index);
    }

    @Override
    public void flip(final long index, final long length) {
        getDelegate().flip(index, length);
    }

    @Override
    public ILargeBitSet optimize() {
        return getDelegate().optimize();
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        return getDelegate().and(others);
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        return getDelegate().andRange(fromInclusive, toExclusive, others);
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        return getDelegate().or(others);
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        return getDelegate().orRange(fromInclusive, toExclusive, others);
    }

    @Override
    public ILargeBitSet negate() {
        return getDelegate().negate();
    }

    @Override
    public ILargeBitSet negateShallow() {
        return getDelegate().negateShallow();
    }

    @Override
    public long getTrueCount() {
        return getDelegate().getTrueCount();
    }

    @Override
    public long size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return getDelegate().newSkippingIndexProvider();
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        getDelegate().getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public ILargeBitSet unwrap() {
        return getDelegate().unwrap();
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        return getDelegate().getBuffer(buffer);
    }

    @Override
    public long getBufferLength() {
        return getDelegate().getBufferLength();
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public void clear(final long index, final long length) {
        getDelegate().clear(index, length);
    }

}
