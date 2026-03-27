package de.invesdwin.util.collections.array.large.bitset.skippingindex;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class AndSkippingLargeIndexProvider implements ISkippingLargeIndexProvider {

    private final PeekingSkippingLargeIndexProvider[] delegates;

    private AndSkippingLargeIndexProvider(final Collection<PeekingSkippingLargeIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingLargeIndexProvider.EMPTY_ARRAY);
    }

    private AndSkippingLargeIndexProvider(final PeekingSkippingLargeIndexProvider... delegates) {
        this.delegates = delegates;
    }

    @Override
    public long next(final long nextCandidate) {
        long max = nextCandidate;
        int i = 0;
        while (i < delegates.length) {
            final long newMax = delegates[i].peek(max);
            if (max < newMax) {
                max = newMax;
                if (i > 0) {
                    //start at 0 again
                    i = 0;
                    continue;
                }
            }
            i++;
        }
        return max;
    }

    public static ISkippingLargeIndexProvider of(final Collection<PeekingSkippingLargeIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new AndSkippingLargeIndexProvider(delegates);
        }
    }

    public static ISkippingLargeIndexProvider of(final PeekingSkippingLargeIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new AndSkippingLargeIndexProvider(delegates);
        }
    }

}
