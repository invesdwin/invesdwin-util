package de.invesdwin.util.collections.loadingcache.historical.internal;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class HistoricalCacheQueryBooster<V> implements IHistoricalCacheQueryBooster<V> {

    private final AHistoricalCache<V> parent;

    public HistoricalCacheQueryBooster(final AHistoricalCache<V> parent) {
        this.parent = parent;
    }

    @Override
    public void clear() {

    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {}

    @Override
    public List<? extends Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        return null;
    }

}
