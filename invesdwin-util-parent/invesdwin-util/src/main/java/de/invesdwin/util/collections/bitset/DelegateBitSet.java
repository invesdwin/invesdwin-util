package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateBitSet extends ADelegateBitSet {

    private IBitSet delegate;

    public DelegateBitSet(final IBitSet delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final IBitSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBitSet getDelegate() {
        return delegate;
    }

}
