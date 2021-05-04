package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCachePreviousKeysQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheInternalMethods<V> {

    IHistoricalCacheRangeQueryInterceptor<V> getRangeQueryInterceptor();

    IHistoricalCachePreviousKeysQueryInterceptor getPreviousKeysQueryInterceptor();

    FDate calculatePreviousKey(FDate key);

    FDate calculateNextKey(FDate key);

    ILoadingCache<FDate, IHistoricalEntry<V>> getValuesMap();

    FDate adjustKey(FDate key);

    void remove(FDate key);

    FDate extractKey(FDate key, V value);

    Integer getMaximumSize();

    void increaseMaximumSize(int maximumSize, String reason);

    IHistoricalCacheQuery<?> newKeysQueryInterceptor();

    IEvaluateGenericFDate<IHistoricalEntry<V>> newComputeEntry();

    IHistoricalCachePutProvider<V> getPutProvider();

    IHistoricalCacheQueryCore<V> getQueryCore();

    IEvaluateGenericFDate<V> newLoadValue();

    FDate innerCalculatePreviousKey(FDate key);

    FDate innerCalculateNextKey(FDate key);

    void invokeRefreshIfRequested();

    void putDirectly(FDate key, IHistoricalEntry<V> value);

    IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider();

    AHistoricalCache<V> getParent();

}
