package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Map.Entry;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheRangeQueryInterceptor<V> {

    ICloseableIterable<FDate> getKeys(FDate from, FDate to);

    ICloseableIterable<Entry<FDate, V>> getEntries(FDate from, FDate to);

}
