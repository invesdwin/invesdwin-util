package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustedFDate extends FDate {

    private final transient IHistoricalCacheAdjustKeyProvider adjustKeyProvider;

    public AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate adjustedKey) {
        super(adjustedKey);
        this.adjustKeyProvider = adjustKeyProvider;
    }

    public IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider() {
        return adjustKeyProvider;
    }

    public static FDate newAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        return new AdjustedFDate(adjustKeyProvider, key);
    }

    public static FDate maybeAdjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            //only when we move to a different adjust key provider
            if (adjustKeyProvider != cKey.adjustKeyProvider) {
                return adjustKeyProvider.adjustKey(cKey);
            } else {
                return cKey;
            }
        } else {
            return adjustKeyProvider.adjustKey(key);
        }
    }

}
