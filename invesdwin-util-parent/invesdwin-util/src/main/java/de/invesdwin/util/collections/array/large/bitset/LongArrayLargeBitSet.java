package de.invesdwin.util.collections.array.large.bitset;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.delegate.ShallowNegatedLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.collections.array.large.buffer.BufferBooleanLargeArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class LongArrayLargeBitSet implements ILargeBitSet {

    public static final Function<LongArrayLargeBitSet, ILargeBitSet> COMPRESSED_COPY_FACTORY = copyFrom -> {
        final long size = copyFrom.size();
        final ILargeBitSet copy = ILockCollectionFactory.getInstance(false).newLargeBitSet(size);
        copyFrom.getBooleans(0, copy, 0, size);
        return copy;
    };
    public static final Function<LongArrayLargeBitSet, ILargeBitSet> DEFAULT_COPY_FACTORY = COMPRESSED_COPY_FACTORY;

    private static final long UNINITIALIZED_TRUE_COUNT = -1;
    private final Function<LongArrayLargeBitSet, ILargeBitSet> copyFactory;
    private final LongArrayLargeBitSetBase bitSet;
    private final long size;
    private long trueCount;

    public LongArrayLargeBitSet(final Function<LongArrayLargeBitSet, ILargeBitSet> copyFactory,
            final LongArrayLargeBitSetBase bitSet, final long size) {
        this.copyFactory = copyFactory;
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    //    public LongArrayLargeBitSet(final int size) {
    //        this.bitSet = new LongArrayLargeBitSetBase(size);
    //        this.size = size;
    //        //leaving trueCount explicitly at 0 so that add works properly
    //    }

    public Function<LongArrayLargeBitSet, ILargeBitSet> getCopyFactory() {
        return copyFactory;
    }

    public LongArrayLargeBitSetBase getBitSet() {
        return bitSet;
    }

    @Override
    public int getId() {
        return bitSet.getId();
    }

    @Override
    public void add(final long index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.set(index);
        } else if (!bitSet.get(index)) {
            bitSet.set(index);
            trueCount++;
        }
    }

    @Override
    public void remove(final long index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.clear(index);
        } else if (bitSet.get(index)) {
            bitSet.clear(index);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final long index) {
        return bitSet.get(index);
    }

    @Override
    public void flip(final long index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.flip(index);
        } else if (bitSet.get(index)) {
            bitSet.clear(index);
            trueCount--;
        } else {
            bitSet.set(index);
            trueCount++;
        }
    }

    @Override
    public void flip(final long index, final long length) {
        bitSet.flip(index, index + length);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    @Override
    public ILargeBitSet optimize() {
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public ILargeBitSet and(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        final ILargeBitSet wrapped = copyFactory.apply(this);
        if (wrapped instanceof LongArrayLargeBitSet) {
            final LongArrayLargeBitSet cWrapped = (LongArrayLargeBitSet) wrapped;
            final LongArrayLargeBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                final ILargeBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayLargeBitSet) {
                    final LongArrayLargeBitSet cUnwrapped = (LongArrayLargeBitSet) unwrapped;
                    combined.and(cUnwrapped.bitSet);
                } else {
                    BitSets.and(wrapped, other);
                }
                if (combined.isEmpty()) {
                    return EmptyLargeBitSet.INSTANCE;
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                BitSets.and(wrapped, other);
                if (wrapped.isEmpty()) {
                    return EmptyLargeBitSet.INSTANCE;
                }
            }
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet andRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final ILargeBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyLargeBitSet.INSTANCE;
            }
        }
        final ILargeBitSet wrapped = copyFactory.apply(this);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        if (wrapped instanceof LongArrayLargeBitSet) {
            final LongArrayLargeBitSet cWrapped = (LongArrayLargeBitSet) wrapped;
            final LongArrayLargeBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                final ILargeBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayLargeBitSet) {
                    final LongArrayLargeBitSet cUnwrapped = (LongArrayLargeBitSet) unwrapped;
                    combined.andRange(cUnwrapped.bitSet, fromInclusive, toExclusive);
                } else {
                    BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
                }
                if (combined.isEmpty()) {
                    return EmptyLargeBitSet.INSTANCE;
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
                if (wrapped.isEmpty()) {
                    return EmptyLargeBitSet.INSTANCE;
                }
            }
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet or(final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final ILargeBitSet wrapped = copyFactory.apply(this);
        if (wrapped instanceof LongArrayLargeBitSet) {
            final LongArrayLargeBitSet cWrapped = (LongArrayLargeBitSet) wrapped;
            final LongArrayLargeBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                final ILargeBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayLargeBitSet) {
                    final LongArrayLargeBitSet cUnwrapped = (LongArrayLargeBitSet) unwrapped;
                    combined.or(cUnwrapped.bitSet);
                } else {
                    BitSets.or(wrapped, other);
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                BitSets.or(wrapped, other);
            }
        }
        if (wrapped.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet orRange(final long fromInclusive, final long toExclusive, final ILargeBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final ILargeBitSet wrapped = copyFactory.apply(this);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        if (wrapped instanceof LongArrayLargeBitSet) {
            final LongArrayLargeBitSet cWrapped = (LongArrayLargeBitSet) wrapped;
            final LongArrayLargeBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                final ILargeBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayLargeBitSet) {
                    final LongArrayLargeBitSet cUnwrapped = (LongArrayLargeBitSet) unwrapped;
                    combined.orRange(cUnwrapped.bitSet, fromInclusive, toExclusive);
                } else {
                    BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final ILargeBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                BitSets.or(wrapped, other);
            }
        }
        if (wrapped.isEmpty()) {
            return EmptyLargeBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public ILargeBitSet negate() {
        final ILargeBitSet negated = copyFactory.apply(this);
        negated.flip(0, size);
        return negated;
    }

    @Override
    public ILargeBitSet negateShallow() {
        return new ShallowNegatedLargeBitSet(this) {
            @Override
            public ISkippingLargeIndexProvider newSkippingIndexProvider() {
                return nextCandidate -> {
                    final long next = bitSet.nextClearBit(nextCandidate);
                    if (next < 0) {
                        return ISkippingLargeIndexProvider.END;
                    } else {
                        return next;
                    }
                };
            }
        };
    }

    @Override
    public long getTrueCount() {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            trueCount = bitSet.cardinality();
        }
        return trueCount;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public ISkippingLargeIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final long next = bitSet.nextSetBit(nextCandidate);
            if (next < 0) {
                return ISkippingLargeIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public void getBooleans(final long srcPos, final ILargeBitSet dest, final long destPos, final long length) {
        if (dest instanceof LongArrayLargeBitSet) {
            final LongArrayLargeBitSet cDest = (LongArrayLargeBitSet) dest.unwrap();
            bitSet.getWords().getLongs(srcPos, cDest.bitSet.getWords(), destPos, length);
        } else {
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public ILargeBitSet unwrap() {
        return this;
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) throws IOException {
        buffer.putLong(BufferBooleanLargeArray.LENGTH_INDEX, size);
        return BufferBooleanLargeArray.ARRAY_INDEX
                + bitSet.getWords().getBuffer(buffer.sliceFrom(BufferBooleanLargeArray.ARRAY_INDEX));
    }

    @Override
    public long getBufferLength() {
        return BufferBooleanLargeArray.ARRAY_INDEX + bitSet.getWords().getBufferLength();
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

    @Override
    public void clear(final long index, final long length) {
        final long toIndexExclusive = index + length;
        bitSet.clear(index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
