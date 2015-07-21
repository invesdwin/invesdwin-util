package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQueryInterceptorSupport<V> implements IHistoricalCacheQueryInterceptor<V> {

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return null;
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        return null;
    }

}
