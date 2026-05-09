package de.invesdwin.util.collections.array.primitive.bitset.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;

@NotThreadSafe
public class DelegatePrimitiveBitSet extends ADelegatePrimitiveBitSet {

    private IPrimitiveBitSet delegate;

    public DelegatePrimitiveBitSet(final IPrimitiveBitSet delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final IPrimitiveBitSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public IPrimitiveBitSet getDelegate() {
        return delegate;
    }

}
