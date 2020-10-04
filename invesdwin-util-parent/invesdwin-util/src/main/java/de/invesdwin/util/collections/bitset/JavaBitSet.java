package de.invesdwin.util.collections.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.BitSets;

@NotThreadSafe
public class JavaBitSet implements IBitSet {

    private final BitSet bitSet;
    private int trueCount = 0;
    private int expectedSize;

    public JavaBitSet(final BitSet bitSet) {
        this.bitSet = bitSet;
        this.trueCount = -1;
    }

    public JavaBitSet(final int expectedSize) {
        this.bitSet = new BitSet(expectedSize);
        //leaving trueCount explicitly at 0 so that add works properly
        this.expectedSize = expectedSize;
    }

    @Override
    public void add(final int index) {
        bitSet.set(index);
        trueCount++;
    }

    @Override
    public void remove(final int index) {
        bitSet.clear(index);
        trueCount = -1;
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.get(index);
    }

    @Override
    public IBitSet optimize() {
        if (isEmpty()) {
            return EmptyBitSet.INSTANCE;
        } else {
            return this;
        }
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            combined.and(cOther.bitSet);
        }
        return new JavaBitSet(combined);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final IBitSet other = others[i];
            if (other.isEmpty()) {
                return EmptyBitSet.INSTANCE;
            }
            final JavaBitSet cOther = (JavaBitSet) other.unwrap();
            BitSets.andRangeFast(combined, cOther.bitSet, fromInclusive, toExclusive);
        }
        return new JavaBitSet(combined);
    }

    @Override
    public IBitSet negate() {
        final BitSet negated = (BitSet) bitSet.clone();
        negated.flip(0, expectedSize);
        return new JavaBitSet(negated);
    }

    @Override
    public int getTrueCount() {
        if (trueCount == -1) {
            trueCount = bitSet.cardinality();
        }
        return trueCount;
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return nextCandidate -> {
            final int next = bitSet.nextSetBit(nextCandidate);
            if (next < 0) {
                return ISkippingIndexProvider.END;
            } else {
                return next;
            }
        };
    }

    @Override
    public IBitSet unwrap() {
        return this;
    }

}
