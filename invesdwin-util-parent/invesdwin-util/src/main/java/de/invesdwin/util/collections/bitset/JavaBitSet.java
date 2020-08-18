package de.invesdwin.util.collections.bitset;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class JavaBitSet implements IBitSet {

    private final BitSet bitSet;

    public JavaBitSet() {
        this.bitSet = new BitSet();
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

}
