package de.invesdwin.util.collections.bitset;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class OrSkippingIndexProvider implements ISkippingIndexProvider {

    private final PeekingSkippingIndexProvider[] delegates;

    private OrSkippingIndexProvider(final Collection<PeekingSkippingIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingIndexProvider.EMPTY_ARRAY);
    }

    private OrSkippingIndexProvider(final PeekingSkippingIndexProvider... delegates) {
        this.delegates = delegates;
    }

    @Override
    public int next(final int nextCandidate) {
        int min = nextCandidate;
        int i = 0;
        while (i < delegates.length) {
            final int newMin = delegates[i].peek(min);
            if (min > newMin) {
                min = newMin;
            }
            i++;
        }
        return min;
    }

    public static ISkippingIndexProvider of(final Collection<PeekingSkippingIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new OrSkippingIndexProvider(delegates);
        }
    }

    public static ISkippingIndexProvider of(final PeekingSkippingIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new OrSkippingIndexProvider(delegates);
        }
    }

}
