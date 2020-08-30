package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class AndSkippingIndexProvider implements ISkippingIndexProvider {

    private final PeekingSkippingIndexProvider[] delegates;

    public AndSkippingIndexProvider(final PeekingSkippingIndexProvider... delegates) {
        this.delegates = delegates;
    }

    @Override
    public int next(final int cur) {
        int max = cur + 1;
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

}
