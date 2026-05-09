package de.invesdwin.util.collections.array.primitive.bitset.skippingindex;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PeekingSkippingPrimitiveIndexProvider implements ISkippingPrimitiveIndexProvider {

    public static final PeekingSkippingPrimitiveIndexProvider[] EMPTY_ARRAY = new PeekingSkippingPrimitiveIndexProvider[0];

    private final ISkippingPrimitiveIndexProvider delegate;

    private int peek = -1;

    public PeekingSkippingPrimitiveIndexProvider(final ISkippingPrimitiveIndexProvider delegate) {
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
