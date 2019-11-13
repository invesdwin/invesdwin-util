package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.AdjustedFDate;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ANonRecursivePullingHistoricalCacheAdjustKeyProvider
        implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;
    private final Set<HistoricalCacheForClear> historicalCachesForClear = Collections
            .synchronizedSet(new HashSet<HistoricalCacheForClear>());
    private final AHistoricalCache<?> parent;
    private final int hashCode = super.hashCode();

    public ANonRecursivePullingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return parent;
    }

    @Override
    public FDate adjustKey(final FDate key) {
        if (key == null) {
            return null;
        }
        if (key.isBeforeOrEqualTo(curHighestAllowedKey)) {
            return key;
        }
        final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
        if (newHighestAllowedKey != null) {
            if (key.millisValue() > newHighestAllowedKey.millisValue()) {
                return newHighestAllowedKey;
            }
        }
        return key;
    }

    private FDate getHighestAllowedKeyUpdateCached() {
        final FDate newHighestAllowedKey = innerGetHighestAllowedKey();
        if (newHighestAllowedKey != null) {
            final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
            final boolean purge = curHighestAllowedKeyCopy == null;
            if (purge) {
                //purge maybe already remembered keys above curHighestAllowedKey
                clear();
            }

            if (purge || curHighestAllowedKeyCopy.isBefore(newHighestAllowedKey)) {
                curHighestAllowedKey = newHighestAllowedKey;
            }
        }
        return newHighestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        if (curHighestAllowedKey == null) {
            final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
            curHighestAllowedKey = newHighestAllowedKey;
        }
        return curHighestAllowedKey;
    }

    protected abstract FDate innerGetHighestAllowedKey();

    @Override
    public void clear() {
        //        curHighestAllowedKey = null; // dont clear highestallowedkey or else backtests might get confused
        if (!historicalCachesForClear.isEmpty()) {
            //make copy to prevent recusion
            final List<HistoricalCacheForClear> historicalCachesForClearCopy = new ArrayList<HistoricalCacheForClear>(
                    historicalCachesForClear);
            //remove references to prevent memory leaks
            historicalCachesForClear.clear();
            for (final HistoricalCacheForClear c : historicalCachesForClearCopy) {
                c.clear();
            }
        }
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        if (curHighestAllowedKey == null) {
            return historicalCachesForClear.add(new HistoricalCacheForClear(historicalCache));
        } else {
            historicalCache.clear();
            return true;
        }
    }

    @Override
    public FDate newAlreadyAdjustedKey(final FDate key) {
        return AdjustedFDate.newAdjustedKey(this, key);
    }

    @Override
    public FDate maybeAdjustKey(final FDate key) {
        return AdjustedFDate.maybeAdjustKey(this, key);
    }

    @Override
    public boolean isAlreadyAdjustingKey() {
        return false;
    }

    @Override
    public final <T> IHistoricalCacheQuery<T> newQuery(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> internalMethods) {
        return new de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust.AdjustingHistoricalCacheQuery<T>(
                internalMethods);
    }

}
