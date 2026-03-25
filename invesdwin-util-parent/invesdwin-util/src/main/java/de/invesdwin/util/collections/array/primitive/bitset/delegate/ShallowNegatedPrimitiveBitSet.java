package de.invesdwin.util.collections.array.primitive.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.EmptyPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.PeekingSkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class ShallowNegatedPrimitiveBitSet implements IPrimitiveBitSet {

    private final IPrimitiveBitSet delegate;
    private final int trueCount;

    public ShallowNegatedPrimitiveBitSet(final IPrimitiveBitSet delegate) {
        this.delegate = delegate;
        this.trueCount = delegate.size() - delegate.getTrueCount();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void add(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final int index) {
        return !delegate.contains(index);
    }

    @Override
    public IPrimitiveBitSet optimize() {
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        final IPrimitiveBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else if (optimized.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        } else {
            return new ShallowNegatedPrimitiveBitSet(optimized);
        }
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
    public IPrimitiveBitSet negate() {
        return delegate;
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return delegate;
    }

    @Override
    public int getTrueCount() {
        return trueCount;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return trueCount == 0;
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        final PeekingSkippingPrimitiveIndexProvider provider = new PeekingSkippingPrimitiveIndexProvider(
                delegate.newSkippingIndexProvider());
        return nextCandidate -> {
            int nextFalseCandidate = nextCandidate;
            while (true) {
                final int nextTrueValue = provider.peek(nextFalseCandidate);
                if (nextTrueValue > nextFalseCandidate) {
                    return nextFalseCandidate;
                } else {
                    nextFalseCandidate++;
                }
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        throw new UnsupportedOperationException();
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
