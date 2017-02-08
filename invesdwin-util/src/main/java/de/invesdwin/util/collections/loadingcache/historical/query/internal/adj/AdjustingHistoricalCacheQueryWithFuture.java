package de.invesdwin.util.collections.loadingcache.historical.query.internal.adj;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustingHistoricalCacheQueryWithFuture<V> extends AdjustingHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    protected AdjustingHistoricalCacheQueryWithFuture(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQueryWithFuture<V> delegate) {
        super(core, delegate);
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.withElementFilter(elementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        delegate.withFilterDuplicateKeys(filterDuplicateKeys);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withRememberNullValue(final boolean rememberNullValue) {
        delegate.withRememberNullValue(rememberNullValue);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        delegate.withFutureNull();
        return this;
    }

    @Override
    public FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextKey(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextKeys(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntry(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextValues(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntries(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextValue(adjustKey(key), shiftForwardUnits);
    }

}
