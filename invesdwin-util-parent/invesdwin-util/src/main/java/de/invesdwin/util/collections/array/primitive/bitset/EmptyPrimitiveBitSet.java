package de.invesdwin.util.collections.array.primitive.bitset;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class EmptyPrimitiveBitSet implements IPrimitiveBitSet {

    public static final EmptyPrimitiveBitSet INSTANCE = new EmptyPrimitiveBitSet();

    @Override
    public int getId() {
        return ID_EMPTY;
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
        return false;
    }

    @Override
    public IPrimitiveBitSet optimize() {
        return this;
    }

    /**
     * We explicitly don't return an always true instance here because it does not add any knowledge and should instead
     * be skipped. A negated empty array is still empty, that is the rationale.
     */
    @Override
    public IPrimitiveBitSet negate() {
        return INSTANCE;
    }

    /**
     * We explicitly don't return an always true instance here because it does not add any knowledge and should instead
     * be skipped. A negated empty array is still empty, that is the rationale.
     */
    @Override
    public IPrimitiveBitSet negateShallow() {
        return INSTANCE;
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        return INSTANCE;
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        return INSTANCE;
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        if (others.length == 0) {
            return INSTANCE;
        } else if (others.length == 1) {
            return others[0];
        } else {
            final IPrimitiveBitSet first = others[0];
            final IPrimitiveBitSet[] rest = new IPrimitiveBitSet[others.length - 1];
            for (int i = 1; i < others.length; i++) {
                rest[i - 1] = others[i];
            }
            return first.or(rest);
        }
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet[] others) {
        if (others.length == 0) {
            return INSTANCE;
        } else if (others.length == 1) {
            return others[0];
        } else {
            final IPrimitiveBitSet first = others[0];
            final IPrimitiveBitSet[] rest = new IPrimitiveBitSet[others.length - 1];
            for (int i = 1; i < others.length; i++) {
                rest[i - 1] = others[i];
            }
            return first.orRange(fromInclusive, toExclusive, rest);
        }
    }

    @Override
    public int getTrueCount() {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> ISkippingPrimitiveIndexProvider.END;
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet values, final int destPos, final int length) {
        //noop
    }

    @Override
    public IPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        return 0;
    }

    @Override
    public int getBufferLength() {
        return 0;
    }

    @Override
    public void clear() {}

}
