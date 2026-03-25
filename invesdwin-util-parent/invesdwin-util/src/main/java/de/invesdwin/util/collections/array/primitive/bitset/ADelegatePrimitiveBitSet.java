package de.invesdwin.util.collections.array.primitive.bitset;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public abstract class ADelegatePrimitiveBitSet implements IPrimitiveBitSet {

    public ADelegatePrimitiveBitSet() {}

    protected abstract IPrimitiveBitSet getDelegate();

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
    public IPrimitiveBitSet optimize() {
        return getDelegate().optimize();
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        return getDelegate().and(others);
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        return getDelegate().andRange(fromInclusive, toExclusive, others);
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        return getDelegate().or(others);
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        return getDelegate().orRange(fromInclusive, toExclusive, others);
    }

    @Override
    public IPrimitiveBitSet negate() {
        return getDelegate().negate();
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
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
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return getDelegate().newSkippingIndexProvider();
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        getDelegate().getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        return getDelegate().unwrap();
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        return getDelegate().getBuffer(buffer);
    }

    @Override
    public int getBufferLength() {
        return getDelegate().getBufferLength();
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

}
