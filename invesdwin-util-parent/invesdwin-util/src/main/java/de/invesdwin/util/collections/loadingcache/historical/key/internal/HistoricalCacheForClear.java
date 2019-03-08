package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.lang.Objects;

/**
 * Need to wrap historicalCache, since bar caches do not have the class in their hashCode and equals, but there might be
 * multiple ones with the same barConfig in individual levels which get aggregated here.
 */
@Immutable
public class HistoricalCacheForClear {

    private final AHistoricalCache<?> cache;

    public HistoricalCacheForClear(final AHistoricalCache<?> cache) {
        this.cache = cache;
    }

    public void clear() {
        cache.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cache, cache.getClass());
    }

    @Override
    public boolean equals(final Object obj) {
        return Objects.equals(cache, obj) && Objects.equals(cache.getClass(), obj.getClass());
    }

}
