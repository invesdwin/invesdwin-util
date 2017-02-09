package de.invesdwin.util.collections.loadingcache.historical.query.internal.adj;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustedFDate extends FDate {

    private final IHistoricalCacheAdjustKeyProvider adjustKeyProvider;

    public AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate adjustedKey) {
        super(adjustedKey);
        this.adjustKeyProvider = adjustKeyProvider;
    }

    public IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider() {
        return adjustKeyProvider;
    }

    public static FDate adjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        return new AdjustedFDate(adjustKeyProvider, adjustKeyProvider.adjustKey(key));
    }

    public static FDate maybeAdjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            //only when we move to a different adjust key provider
            if (adjustKeyProvider.shouldReadjustKey(cKey.adjustKeyProvider)) {
                return AdjustedFDate.adjustKey(adjustKeyProvider, cKey);
            } else {
                return cKey;
            }
        } else {
            return adjustKey(adjustKeyProvider, key);
        }
    }

    public static FDate maybeReadjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            //only when we move to a different adjust key provider
            if (adjustKeyProvider.shouldReadjustKey(cKey.adjustKeyProvider)) {
                return AdjustedFDate.adjustKey(adjustKeyProvider, cKey);
            } else {
                return cKey;
            }
        } else {
            return new AdjustedFDate(adjustKeyProvider, key);
        }
    }

}
