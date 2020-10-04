package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateBitSet implements IBitSet {

    public ADelegateBitSet() {
    }

    protected abstract IBitSet getDelegate();

    @Override
    public void add(final int index) {
        getDelegate().add(index);
    }

    @Override
    public void remove(final int index) {
        getDelegate().remove(index);
    }

    @Override
    public boolean contains(final int index) {
        return getDelegate().contains(index);
    }

    @Override
    public IBitSet optimize() {
        return getDelegate().optimize();
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        return getDelegate().and(others);
    }

    @Override
    public IBitSet andRange(final int fromInclusive, final int toExclusive, final IBitSet[] others) {
        return getDelegate().andRange(fromInclusive, toExclusive, others);
    }

    @Override
    public IBitSet negate() {
        return getDelegate().negate();
    }

    @Override
    public IBitSet negateShallow() {
        return getDelegate().negateShallow();
    }

    @Override
    public int getTrueCount() {
        return getDelegate().getTrueCount();
    }

    @Override
    public int getExpectedSize() {
        return getDelegate().getExpectedSize();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return getDelegate().newSkippingIndexProvider();
    }

    @Override
    public IBitSet unwrap() {
        return getDelegate().unwrap();
    }

}
