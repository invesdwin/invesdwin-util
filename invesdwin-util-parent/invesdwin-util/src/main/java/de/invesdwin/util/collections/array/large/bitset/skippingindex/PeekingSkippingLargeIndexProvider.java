package de.invesdwin.util.collections.array.large.bitset.skippingindex;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingSkippingLargeIndexProvider implements ISkippingLargeIndexProvider {

    public static final PeekingSkippingLargeIndexProvider[] EMPTY_ARRAY = new PeekingSkippingLargeIndexProvider[0];

    private final ISkippingLargeIndexProvider delegate;

    private long peek = -1;

    public PeekingSkippingLargeIndexProvider(final ISkippingLargeIndexProvider delegate) {
        this.delegate = delegate;
    }

    public long peek(final long nextCandidate) {
        if (this.peek < nextCandidate) {
            this.peek = delegate.next(nextCandidate);
        }
        return peek;
    }

    @Override
    public long next(final long nextCandidate) {
        if (peek > nextCandidate) {
            return peek;
        } else {
            return delegate.next(nextCandidate);
        }
    }

}
