package de.invesdwin.util.collections.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class JavaBitSet implements IBitSet {

    private final BitSet bitSet;

    public JavaBitSet() {
        this.bitSet = new BitSet();
    }

    public JavaBitSet(final BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public JavaBitSet(final int expectedSize) {
        this.bitSet = new BitSet(expectedSize);
    }

    @Override
    public void add(final int index) {
        bitSet.set(index);
    }

    @Override
    public void remove(final int index) {
        bitSet.clear(index);
    }

    @Override
    public boolean contains(final int index) {
        return bitSet.get(index);
    }

    @Override
    public void optimize() {
        //noop
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        final BitSet combined = (BitSet) bitSet.clone();
        for (int i = 0; i < others.length; i++) {
            final JavaBitSet cOther = (JavaBitSet) others[i];
            combined.and(cOther.bitSet);
        }
        return new JavaBitSet(combined);
    }

    @Override
    public int getTrueCount() {
        return bitSet.cardinality();
    }

    @Override
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return null;
    }

}
