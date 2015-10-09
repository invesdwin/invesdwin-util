package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class PushingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;
    private final Set<AHistoricalCache<?>> historicalCaches = new CopyOnWriteArraySet<AHistoricalCache<?>>();

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
            for (final AHistoricalCache<?> c : historicalCaches) {
                c.clear();
            }
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
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        return historicalCaches.add(historicalCache);
    }

}
