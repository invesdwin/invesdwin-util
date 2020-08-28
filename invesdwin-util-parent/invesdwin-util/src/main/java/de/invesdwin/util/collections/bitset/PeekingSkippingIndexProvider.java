package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingSkippingIndexProvider implements ISkippingIndexProvider {

    private final ISkippingIndexProvider delegate;

    private int peek = -1;

    public PeekingSkippingIndexProvider(final ISkippingIndexProvider delegate) {
        this.delegate = delegate;
    }

    public int peek(final int cur) {
        if (this.peek < cur) {
            this.peek = delegate.next(cur);
        }
        return peek;
    }

    @Override
    public int next(final int cur) {
        if (peek > cur) {
            return peek;
        } else {
            return delegate.next(cur);
        }
    }

}
