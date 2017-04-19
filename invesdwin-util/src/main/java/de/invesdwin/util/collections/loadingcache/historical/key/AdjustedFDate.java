package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustedFDate extends FDate {

    private final int adjustKeyProviderIdentityHashCode;

    public AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate adjustedKey) {
        super(adjustedKey);
        this.adjustKeyProviderIdentityHashCode = System.identityHashCode(adjustKeyProvider);
    }

    public static FDate newAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        return new AdjustedFDate(adjustKeyProvider, key);
    }

    public static FDate maybeAdjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            //only when we move to a different adjust key provider
            if (System.identityHashCode(adjustKeyProvider) != cKey.adjustKeyProviderIdentityHashCode) {
                return adjustKeyProvider.adjustKey(cKey);
            } else {
                return cKey;
            }
        } else {
            return adjustKeyProvider.adjustKey(key);
        }
    }

}
