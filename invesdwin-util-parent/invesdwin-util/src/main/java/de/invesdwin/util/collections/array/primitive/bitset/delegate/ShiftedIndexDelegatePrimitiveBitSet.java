package de.invesdwin.util.collections.array.primitive.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class ShiftedIndexDelegatePrimitiveBitSet implements IPrimitiveBitSet {

    private final IPrimitiveBitSet delegate;
    private final int subtractFromIndex;

    private ShiftedIndexDelegatePrimitiveBitSet(final IPrimitiveBitSet delegate, final int subtractFromIndex) {
        this.delegate = delegate;
        this.subtractFromIndex = subtractFromIndex;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    private int adjustIndex(final int index) {
        return index - subtractFromIndex;
    }

    private int unadjustIndex(final int index) {
        if (index >= ISkippingPrimitiveIndexProvider.END) {
            return index;
        } else {
            return index + subtractFromIndex;
        }
    }

    @Override
    public void add(final int index) {
        delegate.add(adjustIndex(index));
    }

    @Override
    public void remove(final int index) {
        delegate.remove(adjustIndex(index));
    }

    @Override
    public boolean contains(final int index) {
        return delegate.contains(adjustIndex(index));
    }

    @Override
    public IPrimitiveBitSet optimize() {
        final IPrimitiveBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else {
            return new ShiftedIndexDelegatePrimitiveBitSet(optimized, subtractFromIndex);
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet negate() {
        return of(delegate.negate(), subtractFromIndex);
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return of(delegate.negateShallow(), subtractFromIndex);
    }

    @Override
    public int getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public int size() {
        return delegate.size() - subtractFromIndex;
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        final ISkippingPrimitiveIndexProvider provider = delegate.newSkippingIndexProvider();
        if (provider == null) {
            return null;
        } else {
            return key -> unadjustIndex(provider.next(adjustIndex(key)));
        }
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static IPrimitiveBitSet of(final IPrimitiveBitSet delegate, final int subtractFromIndex) {
        if (subtractFromIndex == 0) {
            return delegate;
        } else {
            return new ShiftedIndexDelegatePrimitiveBitSet(delegate, subtractFromIndex);
        }
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
        delegate.clear();
    }

}
