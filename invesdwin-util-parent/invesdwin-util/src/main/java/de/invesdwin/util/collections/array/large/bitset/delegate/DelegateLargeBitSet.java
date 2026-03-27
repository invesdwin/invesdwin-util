package de.invesdwin.util.collections.array.large.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;

@NotThreadSafe
public class DelegateLargeBitSet extends ADelegateLargeBitSet {

    private ILargeBitSet delegate;

    public DelegateLargeBitSet(final ILargeBitSet delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final ILargeBitSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public ILargeBitSet getDelegate() {
        return delegate;
    }

}
