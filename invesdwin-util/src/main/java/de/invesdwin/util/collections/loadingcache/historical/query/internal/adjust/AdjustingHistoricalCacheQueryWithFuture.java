package de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustingHistoricalCacheQueryWithFuture<V> extends AdjustingHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    public AdjustingHistoricalCacheQueryWithFuture(final IHistoricalCacheQueryCore<V> core) {
        this(core, new HistoricalCacheQueryWithFuture<V>(core));
    }

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
    public IHistoricalEntry<V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntry(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextValues(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntries(adjustKey(key), shiftForwardUnits);
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        return delegate.getNextValue(adjustKey(key), shiftForwardUnits);
    }

    @Override
    protected AdjustingHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
