package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.HistoricalCacheForClear;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class PushingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;
    private final Set<HistoricalCacheForClear> historicalCachesForClear = new CopyOnWriteArraySet<HistoricalCacheForClear>();

    @Override
    public FDate adjustKey(final FDate key) {
        final FDate highestAllowedKey = getHighestAllowedKey();
        if (highestAllowedKey != null && key.isAfter(highestAllowedKey)) {
            return highestAllowedKey;
        } else {
            return key;
        }
    }

    public void pushHighestAllowedKey(final FDate highestAllowedKey) {
        if (curHighestAllowedKey == null && highestAllowedKey != null) {
            clear();
        }
        this.curHighestAllowedKey = highestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        return curHighestAllowedKey;
    }

    @Override
    public void clear() {
        curHighestAllowedKey = null;
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
            //clear now, since next access will be with a valid highest allowed key
            historicalCache.clear();
            return true;
        }
    }

}
