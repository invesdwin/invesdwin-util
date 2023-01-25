package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.date.FDate;

/**
 * Filters away entries that contain null values.
 */
@Immutable
public abstract class AFilteringDelegateHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    protected abstract IHistoricalCacheQueryCore<V> getDelegate();

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        return filter(getDelegate().getPreviousEntry(query, key, shiftBackUnits));
    }

    private IHistoricalEntry<V> filter(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        }
        //check if present to prevent stack overflow during lazy loading
        if (entry.isValuePresent() && entry.getValueIfPresent() == null) {
            if (entry.getKey() != null) {
                if (entry instanceof ImmutableHistoricalEntry) {
                    return entry;
                } else {
                    return ImmutableHistoricalEntry.of(entry.getKey(), null);
                }
            } else {
                return null;
            }
        }
        return entry;
    }

    private ICloseableIterable<IHistoricalEntry<V>> filter(final ICloseableIterable<IHistoricalEntry<V>> entries) {
        return new ASkippingIterable<IHistoricalEntry<V>>(entries) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                if (element == null) {
                    return true;
                }
                /*
                 * converting to ImmutableHistoricalEntry here causes errors because the upstream code has different
                 * expectations. If this becomes a requirement due to specific null values being required, this needs to
                 * be investigated in more detail.
                 */
                if (element.isValuePresent() && element.getValueIfPresent() == null) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        return filter(getDelegate().getPreviousEntries(query, key, shiftBackUnits));
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return getDelegate().getParent();
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        getDelegate().increaseMaximumSize(maximumSize);
    }

    @Override
    public V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return getDelegate().getValue(query, key, assertValue);
    }

    @Override
    public IHistoricalEntry<V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return filter(getDelegate().getEntry(query, key, assertValue));
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        return filter(getDelegate().getNextEntries(query, key, shiftForwardUnits));
    }

    @Override
    public IHistoricalEntry<V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return filter(getDelegate().getNextEntry(query, key, shiftForwardUnits));
    }

    @Override
    public IHistoricalEntry<V> computeEntry(final HistoricalCacheQuery<V> historicalCacheQuery, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return filter(getDelegate().computeEntry(historicalCacheQuery, key, assertValue));
    }

    @Override
    public void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
        getDelegate().putPrevious(previousKey, value, valueKey);
    }

    @Override
    public void putPreviousKey(final FDate previousKey, final FDate valueKey) {
        getDelegate().putPreviousKey(previousKey, valueKey);
    }

}
