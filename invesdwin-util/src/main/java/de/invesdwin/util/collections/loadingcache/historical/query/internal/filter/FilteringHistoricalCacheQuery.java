package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class FilteringHistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private final IHistoricalCacheQueryCore<V> core;
    private final IHistoricalCacheQuery<V> delegate;

    public FilteringHistoricalCacheQuery(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQuery<V> delegate) {
        this.core = core;
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.withElementFilter(elementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withThreadLocalElementFilter(
            final IHistoricalCacheQueryElementFilter<V> threadLocalElementFilter) {
        delegate.withThreadLocalElementFilter(threadLocalElementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        delegate.withFilterDuplicateKeys(filterDuplicateKeys);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withFutureNull() {
        Assertions.checkSame(delegate.withFutureNull(), delegate);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFuture() {
        return newFutureQuery();
    }

    protected FilteringHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return new FilteringHistoricalCacheQueryWithFuture<V>(core, delegate.withFuture());
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
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKey(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKeys(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntry(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getPreviousValue(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValue(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntries(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValues(key, shiftBackUnits);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getKeys(from, to);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getEntries(from, to);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public ICloseableIterable<V> getValues(final FDate from, final FDate to) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getValues(from, to);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public FDate getPreviousKeyWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKeyWithSameValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getPreviousValueWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValueWithSameValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getPreviousEntryWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntryWithSameValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKeyWithDifferentValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getPreviousValueWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValueWithDifferentValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getPreviousEntryWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(to)) {
                    return false;
                }
                if (valueKey.isBefore(from)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntryWithDifferentValueBetween(from, to, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public FDate getPreviousKeyWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKeyWithSameValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getPreviousValueWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValueWithSameValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getPreviousEntryWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntryWithSameValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousKeyWithDifferentValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public V getPreviousValueWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousValueWithDifferentValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @Override
    public Entry<FDate, V> getPreviousEntryWithDifferentValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        final IHistoricalCacheQueryElementFilter<V> existing = delegate.getThreadLocalElementFilter();
        delegate.withThreadLocalElementFilter(new IHistoricalCacheQueryElementFilter<V>() {

            @Override
            public boolean isValid(final FDate valueKey, final V value) {
                if (valueKey.isAfter(key)) {
                    return false;
                }
                return existing.isValid(valueKey, value);
            }
        });
        try {
            return delegate.getPreviousEntryWithDifferentValue(key, maxShiftBackUnits, value);
        } finally {
            delegate.withThreadLocalElementFilter(existing);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void copyQuerySettings(final IHistoricalCacheQuery copyFrom) {
        delegate.copyQuerySettings(copyFrom);
    }

    @Override
    public HistoricalCacheAssertValue getAssertValue() {
        return delegate.getAssertValue();
    }

    @Override
    public boolean isFilterDuplicateKeys() {
        return delegate.isFilterDuplicateKeys();
    }

    @Override
    public IHistoricalCacheQueryElementFilter<V> getElementFilter() {
        return delegate.getElementFilter();
    }

    @Override
    public IHistoricalCacheQueryElementFilter<V> getThreadLocalElementFilter() {
        return delegate.getThreadLocalElementFilter();
    }

    @Override
    public IHistoricalCacheQueryElementFilter<V> getElementFilterWithThreadLocal() {
        return delegate.getElementFilterWithThreadLocal();
    }

    @Override
    public void resetQuerySettings() {
        delegate.resetQuerySettings();
    }

    @Override
    public List<Entry<FDate, V>> newEntriesList(final int size) {
        return delegate.newEntriesList(size);
    }

    @Override
    public Entry<FDate, V> computeEntry(final FDate key) {
        return delegate.computeEntry(key);
    }

    @Override
    public FDate computeKey(final FDate key) {
        return delegate.computeKey(key);
    }

    @Override
    public V computeValue(final FDate key) {
        return delegate.computeValue(key);
    }

}
