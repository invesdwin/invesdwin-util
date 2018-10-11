package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;

public interface IHistoricalCacheQueryInternalMethods<V> {

    HistoricalCacheAssertValue getAssertValue();

    IHistoricalCacheQueryElementFilter<V> getElementFilter();

}
