package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;

public interface IHistoricalCacheQueryInternalMethods<V> {

    HistoricalCacheAssertValue getAssertValue();

    IHistoricalCacheQueryElementFilter<V> getElementFilter();

    List<IHistoricalEntry<V>> newEntriesList(int size);

    boolean isFilterDuplicateKeys();

}
