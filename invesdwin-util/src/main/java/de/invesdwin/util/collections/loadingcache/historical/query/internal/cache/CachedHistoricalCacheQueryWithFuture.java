package de.invesdwin.util.collections.loadingcache.historical.query.internal.cache;

import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.cache.booster.IHistoricalCacheQueryBooster;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class CachedHistoricalCacheQueryWithFuture<V> extends CachedHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    public CachedHistoricalCacheQueryWithFuture(final IHistoricalCacheQueryWithFuture<V> delegate,
            final IHistoricalCacheQueryBooster<V> queryBooster) {
        super(delegate, queryBooster);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> getDelegate() {
        return (IHistoricalCacheQueryWithFuture<V>) super.getDelegate();
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withElementFilter(elementFilter);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withFilterDuplicateKeys(filterDuplicateKeys);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withRememberNullValue(final boolean rememberNullValue) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withRememberNullValue(rememberNullValue);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        return (IHistoricalCacheQueryWithFuture<V>) super.withFutureNull();
    }

    @Override
    public FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextKey(key, shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextKeys(key, shiftForwardUnits);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextEntry(key, shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextValues(key, shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextEntries(key, shiftForwardUnits);
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        return getDelegate().getNextValue(key, shiftForwardUnits);
    }

}
