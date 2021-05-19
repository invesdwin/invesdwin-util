package de.invesdwin.util.collections.bitset;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class OrSkippingIndexProvider implements ISkippingIndexProvider {

    private final PeekingSkippingIndexProvider[] delegates;

    public OrSkippingIndexProvider(final Collection<PeekingSkippingIndexProvider> delegates) {
        this.delegates = delegates.toArray(new PeekingSkippingIndexProvider[delegates.size()]);
    }

    public OrSkippingIndexProvider(final PeekingSkippingIndexProvider... delegates) {
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

}
