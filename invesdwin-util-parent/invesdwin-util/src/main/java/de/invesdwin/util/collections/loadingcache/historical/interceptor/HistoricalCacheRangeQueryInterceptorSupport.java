package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class HistoricalCacheRangeQueryInterceptorSupport<V> implements IHistoricalCacheRangeQueryInterceptor<V> {

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return null;
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final FDate from, final FDate to) {
        return null;
    }

}
