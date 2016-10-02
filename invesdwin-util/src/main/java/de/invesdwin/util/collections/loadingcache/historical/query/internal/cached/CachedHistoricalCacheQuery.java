package de.invesdwin.util.collections.loadingcache.historical.query.internal.cached;

import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class CachedHistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private IHistoricalCacheQuery<V> delegate;

    public CachedHistoricalCacheQuery(final IHistoricalCacheQuery<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate = delegate.withElementFilter(elementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        delegate = delegate.withFilterDuplicateKeys(filterDuplicateKeys);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withRememberNullValue(final boolean rememberNullValue) {
        delegate = delegate.withRememberNullValue(rememberNullValue);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withFutureNull() {
        delegate = delegate.withFutureNull();
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFuture() {
        return new CachedHistoricalCacheQueryWithFuture<V>(delegate.withFuture());
    }

    @Override
    public Entry<FDate, V> getEntry(final FDate key) {
        return delegate.getEntry(key);
    }

    @Override
    public V getValue(final FDate key) {
        return delegate.getValue(key);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getEntries(final Iterable<FDate> keys) {
        return delegate.getEntries(keys);
    }

    @Override
    public ICloseableIterable<V> getValues(final Iterable<FDate> keys) {
        return delegate.getValues(keys);
    }

    @Override
    public FDate getKey(final FDate key) {
        return delegate.getKey(key);
    }

    @Override
    public FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousKey(key, shiftBackUnits);
    }

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousKeys(key, shiftBackUnits);
    }

    @Override
    public Entry<FDate, V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousEntry(key, shiftBackUnits);
    }

    @Override
    public V getPreviousValue(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousValue(key, shiftBackUnits);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousEntries(key, shiftBackUnits);
    }

    @Override
    public ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousValues(key, shiftBackUnits);
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return delegate.getKeys(from, to);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        return delegate.getEntries(from, to);
    }

    @Override
    public ICloseableIterable<V> getValues(final FDate from, final FDate to) {
        return delegate.getValues(from, to);
    }

    @Override
    public FDate getPreviousValueKeyBetween(final FDate from, final FDate to, final V value) {
        return delegate.getPreviousValueKeyBetween(from, to, value);
    }

}
