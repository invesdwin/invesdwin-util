package de.invesdwin.util.collections.array.large.bitset.skippingindex;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class OrSkippingLargeIndexProvider implements ISkippingLargeIndexProvider {

    private final PeekingSkippingLargeIndexProvider[] delegates;

    private OrSkippingLargeIndexProvider(final Collection<PeekingSkippingLargeIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingLargeIndexProvider.EMPTY_ARRAY);
    }

    private OrSkippingLargeIndexProvider(final PeekingSkippingLargeIndexProvider... delegates) {
        this.delegates = delegates;
    }

    @Override
    public long next(final long nextCandidate) {
        long min = nextCandidate;
        int i = 0;
        while (i < delegates.length) {
            final long newMin = delegates[i].peek(min);
            if (min > newMin) {
                min = newMin;
            }
            i++;
        }
        return min;
    }

    public static ISkippingLargeIndexProvider of(final Collection<PeekingSkippingLargeIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new OrSkippingLargeIndexProvider(delegates);
        }
    }

    public static ISkippingLargeIndexProvider of(final PeekingSkippingLargeIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new OrSkippingLargeIndexProvider(delegates);
        }
    }

}
