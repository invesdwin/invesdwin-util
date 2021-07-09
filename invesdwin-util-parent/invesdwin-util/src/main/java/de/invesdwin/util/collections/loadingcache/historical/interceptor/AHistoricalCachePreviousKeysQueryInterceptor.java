package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;

@Immutable
public abstract class AHistoricalCachePreviousKeysQueryInterceptor
        implements IHistoricalCachePreviousKeysQueryInterceptor {

    private final IHistoricalCacheQuery<?> parentQuery;

    public AHistoricalCachePreviousKeysQueryInterceptor(final IHistoricalCacheQuery<?> parentQuery) {
        this.parentQuery = parentQuery;
    }

    @Override
    public abstract Optional<FDate> getPreviousKey(FDate key, int shiftBackUnits);

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        final Optional<FDate> optionalFromKey = getPreviousKey(key, shiftBackUnits);
        if (optionalFromKey == null) {
            return null;
        }
        final FDate fromKey = optionalFromKey.orElseGet(null);
        return parentQuery.getKeys(fromKey, key);
    }

}
