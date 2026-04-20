package de.invesdwin.util.collections.array.large.bitset;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public class EmptyLargeBitSet implements ILargeBitSet {

    public static final EmptyLargeBitSet INSTANCE = new EmptyLargeBitSet();

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void add(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final long index) {
        return false;
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
    public ILargeBitSet optimize() {
        return this;
    }

    /**
     * We explicitly don't return an always true instance here because it does not add any knowledge and should instead
     * be skipped. A negated empty array is still empty, that is the rationale.
     */
    @Override
    public ILargeBitSet negate() {
        return INSTANCE;
    }

    /**
     * We explicitly don't return an always true instance here because it does not add any knowledge and should instead
     * be skipped. A negated empty array is still empty, that is the rationale.
     */
    @Override
    public ILargeBitSet negateShallow() {
        return INSTANCE;
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        return INSTANCE;
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        return INSTANCE;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others.length == 0) {
            return INSTANCE;
        } else if (others.length == 1) {
            return others[0];
        } else {
            final ILargeBitSet first = others[0];
            final ILargeBitSet[] rest = new ILargeBitSet[others.length - 1];
            for (int i = 1; i < others.length; i++) {
                rest[i - 1] = others[i];
            }
            return first.or(rest);
        }
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (others.length == 0) {
            return INSTANCE;
        } else if (others.length == 1) {
            return others[0];
        } else {
            final ILargeBitSet first = others[0];
            final ILargeBitSet[] rest = new ILargeBitSet[others.length - 1];
            for (int i = 1; i < others.length; i++) {
                rest[i - 1] = others[i];
            }
            return first.orRange(fromInclusive, toExclusive, rest);
        }
    }

    @Override
    public long getTrueCount() {
        return 0;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> ISkippingPrimitiveIndexProvider.END;
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet values, final long destPos, final long length) {
        //noop
    }

    @Override
    public ILargeBitSet unwrap() {
        return this;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        return 0;
    }

    @Override
    public long getBufferLength() {
        return 0;
    }

    @Override
    public void clear() {}

    @Override
    public void clear(final long index, final long length) {}

}
