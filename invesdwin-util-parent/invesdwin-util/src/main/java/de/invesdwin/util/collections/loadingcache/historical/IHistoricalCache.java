package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheIncreaseMaximumSizeListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.IFDateProvider;

public interface IHistoricalCache<V> extends IHistoricalCacheIncreaseMaximumSizeListener, IHistoricalCachePutListener {

    /**
     * Return false to get a faster implementation
     */
    boolean isThreadSafe();

    /**
     * Use a different type of query core that works faster with limited unstable recursive queries, but minimally
     * slower with normal queries.
     */
    void enableTrailingQueryCore();

    IHistoricalCachePutProvider<V> getPutProvider();

    IHistoricalCacheExtractKeyProvider<V> getExtractKeyProvider();

    /**
     * Keys should be aligned when cache misses are especially expensive. E.g. for recursive queries.
     */
    void setAlignKeys(boolean alignKeys);

    boolean isAlignKeys();

    long getLastRefreshMillis();

    /**
     * Should return the key if the value does not contain a key itself. The time should be the end time for bars.
     */
    FDate extractKey(IFDateProvider key, V value);

    IHistoricalCacheShiftKeyProvider<V> getShiftKeyProvider();

    IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider();

    /**
     * Does not allow values from future per default.
     */
    IHistoricalCacheQuery<V> query();

    boolean containsKey(FDate key);

    void clear();

    Set<IHistoricalCacheOnClearListener> getOnClearListeners();

    boolean registerOnClearListener(IHistoricalCacheOnClearListener l);

    boolean unregisterOnClearListener(IHistoricalCacheOnClearListener l);

    Set<IHistoricalCacheIncreaseMaximumSizeListener> getIncreaseMaximumSizeListeners();

    boolean registerIncreaseMaximumSizeListener(IHistoricalCacheIncreaseMaximumSizeListener l);

    boolean unregisterIncreaseMaximumSizeListener(IHistoricalCacheIncreaseMaximumSizeListener l);

    void preloadData(ExecutorService executor);

    int size();

    <T> T unwrap(Class<T> type);

}
