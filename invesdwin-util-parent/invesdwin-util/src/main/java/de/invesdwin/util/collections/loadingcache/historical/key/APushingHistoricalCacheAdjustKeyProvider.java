package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.ANonRecursivePullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.ARecursivePullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.HistoricalCacheForClear;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class APushingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;
    private volatile FDate prevHighestAllowedKey;
    private final Set<HistoricalCacheForClear> historicalCachesForClear = Collections
            .synchronizedSet(new HashSet<HistoricalCacheForClear>());
    private final IHistoricalCacheAdjustKeyProvider pullingAdjustKeyProvider;
    private final int hashCode = super.hashCode();

    public APushingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        if (isPullingRecursive()) {
            this.pullingAdjustKeyProvider = new ARecursivePullingHistoricalCacheAdjustKeyProvider(parent) {
                @Override
                protected FDate innerGetHighestAllowedKey() {
                    return getInitialHighestAllowedKey();
                }
            };
        } else {
            this.pullingAdjustKeyProvider = new ANonRecursivePullingHistoricalCacheAdjustKeyProvider(parent) {

                @Override
                protected FDate innerGetHighestAllowedKey() {
                    return getInitialHighestAllowedKey();
                }

            };
        }
    }

    protected abstract boolean isPullingRecursive();

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
        return pullingAdjustKeyProvider.getParent();
    }

    @Override
    public boolean isAlreadyAdjustingKey() {
        return pullingAdjustKeyProvider.isAlreadyAdjustingKey();
    }

    @Override
    public FDate adjustKey(final FDate key) {
        if (key == null) {
            return null;
        }
        final FDate highestAllowedKey = getHighestAllowedKey(true);
        if (key.isAfter(highestAllowedKey)) {
            return highestAllowedKey;
        } else {
            return key;
        }
    }

    protected abstract FDate getInitialHighestAllowedKey();

    public void pushHighestAllowedKey(final FDate highestAllowedKey) {
        final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
        if (curHighestAllowedKeyCopy == null && highestAllowedKey != null) {
            clear();
        }
        this.prevHighestAllowedKey = curHighestAllowedKeyCopy;
        this.curHighestAllowedKey = highestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey(final boolean update) {
        final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
        if (curHighestAllowedKeyCopy != null) {
            //already pushed, so keep that
            return curHighestAllowedKeyCopy;
        }
        //keep pulling until first push
        return pullingAdjustKeyProvider.getHighestAllowedKey(update);
    }

    @Override
    public FDate getPreviousHighestAllowedKey() {
        final FDate prevHighestAllowedKeyCopy = prevHighestAllowedKey;
        if (prevHighestAllowedKeyCopy != null) {
            //already pushed, so keep that
            return prevHighestAllowedKeyCopy;
        }
        //keep pulling until first push
        return pullingAdjustKeyProvider.getPreviousHighestAllowedKey();
    }

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
            pullingAdjustKeyProvider.clear();
        }
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        if (curHighestAllowedKey == null) {
            return historicalCachesForClear.add(new HistoricalCacheForClear(historicalCache));
        } else {
            //clear now, since next access will be with a valid highest allowed key
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
    public boolean isAdjustedKey(final FDate key) {
        return AdjustedFDate.isAdjustedKey(this, key);
    }

    @Override
    public final <T> IHistoricalCacheQuery<T> newQuery(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> internalMethods) {
        return new de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust.AdjustingHistoricalCacheQuery<T>(
                internalMethods);
    }

}
