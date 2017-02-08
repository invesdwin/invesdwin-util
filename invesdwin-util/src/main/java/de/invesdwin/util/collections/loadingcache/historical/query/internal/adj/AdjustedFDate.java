package de.invesdwin.util.collections.loadingcache.historical.query.internal.adj;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class AdjustedFDate extends FDate {

    private final IHistoricalCacheAdjustKeyProvider adjustKeyProvider;

    private AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        super(adjustKeyProvider.adjustKey(key));
        this.adjustKeyProvider = adjustKeyProvider;
    }

    public IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider() {
        return adjustKeyProvider;
    }

    public static boolean shouldAdjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            //only when we move to a different adjust key provider
            return adjustKeyProvider != cKey.adjustKeyProvider;
        } else {
            //not adjusted yet
            return true;
        }
    }

    public static FDate adjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        return new AdjustedFDate(adjustKeyProvider, key);
    }

}
