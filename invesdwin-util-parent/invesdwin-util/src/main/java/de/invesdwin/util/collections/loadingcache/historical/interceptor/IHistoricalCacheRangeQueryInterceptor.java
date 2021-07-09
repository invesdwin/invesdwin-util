package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheRangeQueryInterceptor<V> {

    ICloseableIterable<FDate> getKeys(FDate from, FDate to);

    ICloseableIterable<IHistoricalEntry<V>> getEntries(FDate from, FDate to);

}
