package de.invesdwin.util.collections.array.primitive.bitset;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.delegate.ShallowNegatedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
import de.invesdwin.util.collections.array.primitive.buffer.BufferBooleanPrimitiveArray;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class LongArrayPrimitiveBitSet implements IPrimitiveBitSet {

    public static final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> COMPRESSED_COPY_FACTORY = copyFrom -> {
        final int size = copyFrom.size();
        final IPrimitiveBitSet copy = ILockCollectionFactory.getInstance(false).newPrimitiveBitSet(size);
        copyFrom.getBooleans(0, copy, 0, size);
        return copy;
    };
    public static final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> HEAP_COPY_FACTORY = copyFrom -> {
        final int size = copyFrom.size();
        final LongArrayPrimitiveBitSetBase copyBase = new LongArrayPrimitiveBitSetBase(
                ILongPrimitiveArray.newInstance(copyFrom.getBitSet().getWords().asArrayCopy()), size);
        final LongArrayPrimitiveBitSet copy = new LongArrayPrimitiveBitSet(copyFrom.getCopyFactory(), copyBase, size);
        return copy;
    };
    public static final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> DEFAULT_COPY_FACTORY = COMPRESSED_COPY_FACTORY;

    private static final int UNINITIALIZED_TRUE_COUNT = -1;
    private final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> copyFactory;
    private final LongArrayPrimitiveBitSetBase bitSet;
    private final int size;
    private int trueCount;

    public LongArrayPrimitiveBitSet(final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> copyFactory,
            final LongArrayPrimitiveBitSetBase bitSet, final int size) {
        this.copyFactory = copyFactory;
        this.bitSet = bitSet;
        this.size = size;
        this.trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    public LongArrayPrimitiveBitSet(final Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> copyFactory,
            final int size) {
        this.copyFactory = copyFactory;
        this.bitSet = new LongArrayPrimitiveBitSetBase(size);
        this.size = size;
        //leaving trueCount explicitly at 0 so that add works properly
    }

    public LongArrayPrimitiveBitSetBase getBitSet() {
        return bitSet;
    }

    public Function<LongArrayPrimitiveBitSet, IPrimitiveBitSet> getCopyFactory() {
        return copyFactory;
    }

    @Override
    public int getId() {
        return bitSet.getId();
    }

    @Override
    public void add(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.set(index);
        } else if (!bitSet.get(index)) {
            bitSet.set(index);
            trueCount++;
        }
    }

    @Override
    public void remove(final int index) {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            bitSet.clear(index);
        } else if (bitSet.get(index)) {
            bitSet.clear(index);
            trueCount--;
        }
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.get(index);
    }

    @Override
    public void flip(final int index) {
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
    public void flip(final int index, final int length) {
        bitSet.flip(index, index + length);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

    @Override
    public IPrimitiveBitSet optimize() {
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IPrimitiveBitSet and(final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        final IPrimitiveBitSet wrapped = copyFactory.apply(this);
        if (wrapped instanceof LongArrayPrimitiveBitSet) {
            final LongArrayPrimitiveBitSet cWrapped = (LongArrayPrimitiveBitSet) wrapped;
            final LongArrayPrimitiveBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                final IPrimitiveBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayPrimitiveBitSet) {
                    final LongArrayPrimitiveBitSet cUnwrapped = (LongArrayPrimitiveBitSet) unwrapped;
                    combined.and(cUnwrapped.bitSet);
                } else {
                    BitSets.and(wrapped, other);
                }
                if (combined.isEmpty()) {
                    return EmptyPrimitiveBitSet.INSTANCE;
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                BitSets.and(wrapped, other);
                if (wrapped.isEmpty()) {
                    return EmptyPrimitiveBitSet.INSTANCE;
                }
            }
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet andRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        if (isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        for (int i = 0; i < others.length; i++) {
            final IPrimitiveBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyPrimitiveBitSet.INSTANCE;
            }
        }
        final IPrimitiveBitSet wrapped = copyFactory.apply(this);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        if (wrapped instanceof LongArrayPrimitiveBitSet) {
            final LongArrayPrimitiveBitSet cWrapped = (LongArrayPrimitiveBitSet) wrapped;
            final LongArrayPrimitiveBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                final IPrimitiveBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayPrimitiveBitSet) {
                    final LongArrayPrimitiveBitSet cUnwrapped = (LongArrayPrimitiveBitSet) unwrapped;
                    combined.andRange(cUnwrapped.bitSet, fromInclusive, toExclusive);
                } else {
                    BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
                }
                if (combined.isEmpty()) {
                    return EmptyPrimitiveBitSet.INSTANCE;
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                BitSets.andRange(wrapped, other, fromInclusive, toExclusive);
                if (wrapped.isEmpty()) {
                    return EmptyPrimitiveBitSet.INSTANCE;
                }
            }
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet or(final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final IPrimitiveBitSet wrapped = copyFactory.apply(this);
        if (wrapped instanceof LongArrayPrimitiveBitSet) {
            final LongArrayPrimitiveBitSet cWrapped = (LongArrayPrimitiveBitSet) wrapped;
            final LongArrayPrimitiveBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                final IPrimitiveBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayPrimitiveBitSet) {
                    final LongArrayPrimitiveBitSet cUnwrapped = (LongArrayPrimitiveBitSet) unwrapped;
                    combined.or(cUnwrapped.bitSet);
                } else {
                    BitSets.or(wrapped, other);
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                BitSets.or(wrapped, other);
            }
        }
        if (wrapped.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet orRange(final int fromInclusive, final int toExclusive, final IPrimitiveBitSet... others) {
        if (others == null || others.length == 0) {
            return this;
        }
        final IPrimitiveBitSet wrapped = copyFactory.apply(this);
        //remove values outside of range
        wrapped.clear(0, fromInclusive);
        wrapped.clear(toExclusive, size - toExclusive);

        if (wrapped instanceof LongArrayPrimitiveBitSet) {
            final LongArrayPrimitiveBitSet cWrapped = (LongArrayPrimitiveBitSet) wrapped;
            final LongArrayPrimitiveBitSetBase combined = cWrapped.getBitSet();
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                final IPrimitiveBitSet unwrapped = other.unwrap();
                if (unwrapped instanceof LongArrayPrimitiveBitSet) {
                    final LongArrayPrimitiveBitSet cUnwrapped = (LongArrayPrimitiveBitSet) unwrapped;
                    combined.orRange(cUnwrapped.bitSet, fromInclusive, toExclusive);
                } else {
                    BitSets.orRange(wrapped, other, fromInclusive, toExclusive);
                }
            }
        } else {
            for (int i = 0; i < others.length; i++) {
                final IPrimitiveBitSet other = others[i];
                if (other.isEmpty()) {
                    continue;
                }
                BitSets.or(wrapped, other);
            }
        }
        if (wrapped.isEmpty()) {
            return EmptyPrimitiveBitSet.INSTANCE;
        }
        return wrapped;
    }

    @Override
    public IPrimitiveBitSet negate() {
        final IPrimitiveBitSet negated = copyFactory.apply(this);
        negated.flip(0, size);
        return negated;
    }

    @Override
    public IPrimitiveBitSet negateShallow() {
        return new ShallowNegatedPrimitiveBitSet(this) {
            @Override
            public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
                return nextCandidate -> {
                    final int next = bitSet.nextClearBit(nextCandidate);
                    if (next < 0) {
                        return ISkippingPrimitiveIndexProvider.END;
                    } else {
                        return next;
                    }
                };
            }
        };
    }

    @Override
    public int getTrueCount() {
        if (trueCount == UNINITIALIZED_TRUE_COUNT) {
            trueCount = bitSet.cardinality();
        }
        return trueCount;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public ISkippingPrimitiveIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final int next = bitSet.nextSetBit(nextCandidate);
            if (next < 0) {
                return ISkippingPrimitiveIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public void getBooleans(final int srcPos, final IPrimitiveBitSet dest, final int destPos, final int length) {
        if (dest instanceof LongArrayPrimitiveBitSet) {
            final LongArrayPrimitiveBitSet cDest = (LongArrayPrimitiveBitSet) dest.unwrap();
            bitSet.getWords().getLongs(srcPos, cDest.bitSet.getWords(), destPos, length);
        } else {
            BitSets.getBooleans(this, srcPos, dest, destPos, length);
        }
    }

    @Override
    public LongArrayPrimitiveBitSet unwrap() {
        return this;
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) throws IOException {
        buffer.putInt(BufferBooleanPrimitiveArray.LENGTH_INDEX, size);
        return BufferBooleanPrimitiveArray.ARRAY_INDEX
                + bitSet.getWords().getBuffer(buffer.sliceFrom(BufferBooleanPrimitiveArray.ARRAY_INDEX));
    }

    @Override
    public int getBufferLength() {
        return BufferBooleanPrimitiveArray.ARRAY_INDEX + bitSet.getWords().getBufferLength();
    }

    @Override
    public void clear() {
        bitSet.clear();
        trueCount = 0;
    }

    @Override
    public void clear(final int index, final int length) {
        final int toIndexExclusive = index + length;
        bitSet.clear(index, toIndexExclusive);
        trueCount = UNINITIALIZED_TRUE_COUNT;
    }

}
