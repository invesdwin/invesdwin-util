package de.invesdwin.util.collections.loadingcache.historical.query;

import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheQueryElementFilter<V> {

    boolean isValid(FDate valueKey, V value);

}
