package de.invesdwin.util.collections.loadingcache.historical.query.recursive;

import de.invesdwin.util.time.fdate.FDate;

public interface IRecursiveHistoricalCacheQuery<V> {

    void clear();

    int getRecursionCount();

    Integer getUnstableRecursionCount();

    V getPreviousValue(FDate key, FDate previousKey);

}
