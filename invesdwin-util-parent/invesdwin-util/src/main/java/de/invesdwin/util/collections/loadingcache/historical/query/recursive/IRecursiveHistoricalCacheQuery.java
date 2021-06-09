package de.invesdwin.util.collections.loadingcache.historical.query.recursive;

import de.invesdwin.util.time.fdate.FDate;

public interface IRecursiveHistoricalCacheQuery<V> {

    void clear();

    int getRecursionCount();

    Integer getUnstableRecursionCount();

    /**
     * Retrieves or loads the previous value recursively
     */
    V getPreviousValue(FDate key, FDate previousKey);

    /**
     * Retrieves the previous value if it was already loaded. Returns null during recursive loading.
     */
    V getPreviousValueIfPresent(FDate key, FDate previousKey);

    FDate getKey(FDate key);

    /**
     * A limit to not exceed when loading initial value
     */
    FDate getRecursionFrom();

}
