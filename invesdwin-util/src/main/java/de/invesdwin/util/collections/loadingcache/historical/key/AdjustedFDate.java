package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class AdjustedFDate extends IndexedFDate {

    private final int adjustKeyProviderIdentityHashCode;

    public AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate adjustedKey) {
        super(adjustedKey);
        this.adjustKeyProviderIdentityHashCode = System.identityHashCode(adjustKeyProvider);
    }

    public static FDate newAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key == null) {
            return null;
        }
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
