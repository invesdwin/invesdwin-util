package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class FilteringHistoricalCacheQueryWithFuture<V> extends FilteringHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    protected FilteringHistoricalCacheQueryWithFuture(final IHistoricalCacheQueryCore<V> core,
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
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextKey(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextKeys(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextEntry(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextValues(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextEntries(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isBefore(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getNextValue(key, shiftForwardUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    protected FilteringHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
