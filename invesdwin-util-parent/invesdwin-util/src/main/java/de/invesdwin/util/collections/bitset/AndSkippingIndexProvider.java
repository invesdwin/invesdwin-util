package de.invesdwin.util.collections.bitset;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class AndSkippingIndexProvider implements ISkippingIndexProvider {

    private final PeekingSkippingIndexProvider[] delegates;

    private AndSkippingIndexProvider(final Collection<PeekingSkippingIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingIndexProvider.EMPTY_ARRAY);
    }

    private AndSkippingIndexProvider(final PeekingSkippingIndexProvider... delegates) {
        this.delegates = delegates;
    }

    @Override
    public int next(final int nextCandidate) {
        int max = nextCandidate;
        int i = 0;
        while (i < delegates.length) {
            final int newMax = delegates[i].peek(max);
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

    public static ISkippingIndexProvider of(final Collection<PeekingSkippingIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new AndSkippingIndexProvider(delegates);
        }
    }

    public static ISkippingIndexProvider of(final PeekingSkippingIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new AndSkippingIndexProvider(delegates);
        }
    }

}
