package de.invesdwin.util.collections.array.primitive.bitset.skippingindex;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class OrSkippingPrimitiveIndexProvider implements ISkippingPrimitiveIndexProvider {

    private final PeekingSkippingPrimitiveIndexProvider[] delegates;

    private OrSkippingPrimitiveIndexProvider(final Collection<PeekingSkippingPrimitiveIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingPrimitiveIndexProvider.EMPTY_ARRAY);
    }

    private OrSkippingPrimitiveIndexProvider(final PeekingSkippingPrimitiveIndexProvider... delegates) {
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

    public static ISkippingPrimitiveIndexProvider of(final Collection<PeekingSkippingPrimitiveIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new OrSkippingPrimitiveIndexProvider(delegates);
        }
    }

    public static ISkippingPrimitiveIndexProvider of(final PeekingSkippingPrimitiveIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new OrSkippingPrimitiveIndexProvider(delegates);
        }
    }

}
