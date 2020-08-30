package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateBitSet implements IBitSet {

    private IBitSet delegate;

    public DelegateBitSet(final IBitSet delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final IBitSet delegate) {
        this.delegate = delegate;
    }

    public IBitSet getDelegate() {
        return delegate;
    }

    @Override
    public void add(final int index) {
        delegate.add(index);
    }

    @Override
    public void remove(final int index) {
        delegate.remove(index);
    }

    @Override
    public boolean contains(final int index) {
        return delegate.contains(index);
    }

    @Override
    public IBitSet optimize() {
        return delegate.optimize();
    }

    @Override
    public IBitSet and(final IBitSet... others) {
        return delegate.and(others);
    }

    @Override
    public int getTrueCount() {
        return delegate.getTrueCount();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ISkippingIndexProvider newSkippingIndexProvider() {
        return delegate.newSkippingIndexProvider();
    }

    @Override
    public IBitSet unwrap() {
        return delegate.unwrap();
    }

}
