package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.HistoricalCacheForClear;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class APushingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;
    private final Set<HistoricalCacheForClear> historicalCachesForClear = Collections
            .synchronizedSet(new HashSet<HistoricalCacheForClear>());
    private final APullingHistoricalCacheAdjustKeyProvider pullingAdjustKeyProvider;

    public APushingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        this.pullingAdjustKeyProvider = new APullingHistoricalCacheAdjustKeyProvider(parent) {
            @Override
            protected FDate innerGetHighestAllowedKey() {
                return getInitialHighestAllowedKey();
            }
        };
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return pullingAdjustKeyProvider.getParent();
    }

    @Override
    public FDate adjustKey(final FDate key) {
        final FDate highestAllowedKey = getHighestAllowedKey();
        if (highestAllowedKey != null && key.isAfter(highestAllowedKey)) {
            return highestAllowedKey;
        } else {
            return key;
        }
    }

    protected abstract FDate getInitialHighestAllowedKey();

    public void pushHighestAllowedKey(final FDate highestAllowedKey) {
        if (curHighestAllowedKey == null && highestAllowedKey != null) {
            clear();
        }
        this.curHighestAllowedKey = highestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        if (curHighestAllowedKey == null) {
            curHighestAllowedKey = pullingAdjustKeyProvider.getHighestAllowedKey();
        }
        return curHighestAllowedKey;
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

}
