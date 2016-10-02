package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Collection;
import java.util.Map.Entry;

import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQueryInternalMethods<V> {

    boolean isRememberNullValue();

    HistoricalCacheAssertValue getAssertValue();

    IHistoricalCacheQueryElementFilter<V> getElementFilter();

    Collection<Entry<FDate, V>> newEntriesCollection();

}
