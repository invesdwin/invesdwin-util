package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class AdjustedFDate extends IndexedFDate {

    private final int adjustKeyProviderIdentityHashCode;

    public AdjustedFDate(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate adjustedKey) {
        super(adjustedKey);
        this.adjustKeyProviderIdentityHashCode = adjustKeyProvider.hashCode();
    }

    public static FDate newAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key == null) {
            return null;
        }
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            if (cKey.adjustKeyProviderIdentityHashCode == adjustKeyProvider.hashCode()) {
                return cKey;
            }
        }
        return new AdjustedFDate(adjustKeyProvider, key);
    }

    @SuppressWarnings("deprecation")
    public static FDate maybeAdjustKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key == null) {
            return null;
        }
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            return maybeAdjust(adjustKeyProvider, cKey);
        } else {
            final Object extension = key.getExtension();
            if (extension instanceof AdjustedFDate) {
                final AdjustedFDate cKey = (AdjustedFDate) extension;
                return maybeAdjust(adjustKeyProvider, cKey);
            } else {
                return adjustKeyProvider.adjustKey(key);
            }
        }
    }

    private static FDate maybeAdjust(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider,
            final AdjustedFDate cKey) {
        //only when we move to a different adjust key provider
        if (adjustKeyProvider.hashCode() != cKey.adjustKeyProviderIdentityHashCode) {
            return adjustKeyProvider.adjustKey(cKey);
        } else {
            return cKey;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean isAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final FDate key) {
        if (key == null) {
            return false;
        }
        if (key instanceof AdjustedFDate) {
            final AdjustedFDate cKey = (AdjustedFDate) key;
            return isAdjustedKey(adjustKeyProvider, cKey);
        } else {
            final Object extension = key.getExtension();
            if (extension instanceof AdjustedFDate) {
                final AdjustedFDate cKey = (AdjustedFDate) extension;
                return isAdjustedKey(adjustKeyProvider, cKey);
            } else {
                return false;
            }
        }
    }

    private static boolean isAdjustedKey(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider,
            final AdjustedFDate cKey) {
        return adjustKeyProvider.hashCode() == cKey.adjustKeyProviderIdentityHashCode;
    }

}
