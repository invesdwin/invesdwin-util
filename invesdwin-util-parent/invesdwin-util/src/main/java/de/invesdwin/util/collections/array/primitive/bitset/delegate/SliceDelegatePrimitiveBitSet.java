package de.invesdwin.util.collections.array.primitive.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * WARNING: this seems to disable cpu prefetching somehow? Performance is crawling slow.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public final class SliceDelegatePrimitiveBitSet implements IPrimitiveBitSet {

    private final IPrimitiveBitSet delegate;
    private final int from;
    private final int length;

    private SliceDelegatePrimitiveBitSet(final IPrimitiveBitSet delegate, final int from, final int length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    private int adjustIndex(final int index) {
        return index + from;
    }

    private void assertLength(final int index, final int length) {
        final int remaining = this.length - index;
        if (length > remaining) {
            throw FastIndexOutOfBoundsException.getInstance(
                    "Length [%s] exceeds remaining length [%s] at index [%s] and total length [%s]", length, remaining,
                    index, this.length);
        }
    }

    private int unadjustIndex(final int index) {
        if (index >= ISkippingPrimitiveIndexProvider.END) {
            return index;
        } else {
            return index - from;
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
    public void flip(final int index) {
        delegate.flip(adjustIndex(index));
    }

    @Override
    public void flip(final int index, final int length) {
        assertLength(index, length);
        delegate.flip(adjustIndex(index), length);
    }

    @Override
    public IPrimitiveBitSet optimize() {
        final IPrimitiveBitSet optimized = delegate.optimize();
        if (optimized == delegate) {
            return this;
        } else {
            return new SliceDelegatePrimitiveBitSet(optimized, from, length);
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    @Override
    public IPrimitiveBitSet negate() {
        return of(delegate.negate(), from, length);
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return of(delegate.negateShallow(), from, length);
    }

    @Override
    public int getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public int size() {
        return Integers.min(delegate.size() - from, length);
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
        assertLength(srcPos, length);
        delegate.getBooleans(srcPos + from, dest, destPos, length);
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        throw new UnsupportedOperationException("Indexes might get mangled");
    }

    public static IPrimitiveBitSet of(final IPrimitiveBitSet delegate, final int from, final int length) {
        if (from == 0 && length == delegate.size()) {
            return delegate;
        } else {
            return new SliceDelegatePrimitiveBitSet(delegate, from, length);
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
        clear(from, length);
    }

    @Override
    public void clear(final int index, final int length) {
        assertLength(index, length);
        delegate.clear(adjustIndex(index), length);
    }

}
