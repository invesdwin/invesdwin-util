package de.invesdwin.util.collections.array.primitive.bitset.skippingindex;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class AndSkippingPrimitiveIndexProvider implements ISkippingPrimitiveIndexProvider {

    private final PeekingSkippingPrimitiveIndexProvider[] delegates;

    private AndSkippingPrimitiveIndexProvider(final Collection<PeekingSkippingPrimitiveIndexProvider> delegates) {
        this.delegates = delegates.toArray(PeekingSkippingPrimitiveIndexProvider.EMPTY_ARRAY);
    }

    private AndSkippingPrimitiveIndexProvider(final PeekingSkippingPrimitiveIndexProvider... delegates) {
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

    public static ISkippingPrimitiveIndexProvider of(final Collection<PeekingSkippingPrimitiveIndexProvider> delegates) {
        if (delegates == null || delegates.isEmpty()) {
            return null;
        } else if (delegates.size() == 1) {
            return delegates.iterator().next();
        } else {
            return new AndSkippingPrimitiveIndexProvider(delegates);
        }
    }

    public static ISkippingPrimitiveIndexProvider of(final PeekingSkippingPrimitiveIndexProvider... delegates) {
        if (delegates == null || delegates.length == 0) {
            return null;
        } else if (delegates.length == 1) {
            return delegates[0];
        } else {
            return new AndSkippingPrimitiveIndexProvider(delegates);
        }
    }

}
