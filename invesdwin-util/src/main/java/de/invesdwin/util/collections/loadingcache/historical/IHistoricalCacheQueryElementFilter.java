package de.invesdwin.util.collections.loadingcache.historical;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQueryElementFilter<V> {

    boolean isValid(FDate key, V value);

}
