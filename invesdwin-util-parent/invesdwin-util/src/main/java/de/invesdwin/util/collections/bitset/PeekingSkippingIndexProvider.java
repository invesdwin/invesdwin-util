package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingSkippingIndexProvider implements ISkippingIndexProvider {

    private final ISkippingIndexProvider delegate;

    private int peek = -1;

    public PeekingSkippingIndexProvider(final ISkippingIndexProvider delegate) {
        this.delegate = delegate;
    }

    public int peek(final int nextCandidate) {
        if (this.peek < nextCandidate) {
            this.peek = delegate.next(nextCandidate);
        }
        return peek;
    }

    @Override
    public int next(final int nextCandidate) {
        if (peek > nextCandidate) {
            return peek;
        } else {
            return delegate.next(nextCandidate);
        }
    }

}
