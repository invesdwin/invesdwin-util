package de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjustingHistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private final IHistoricalCacheQueryCore<V> core;
    private final IHistoricalCacheQuery<V> delegate;

    public AdjustingHistoricalCacheQuery(final IHistoricalCacheQueryCore<V> core) {
        this(core, new HistoricalCacheQuery<V>(core));
    }

    protected AdjustingHistoricalCacheQuery(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQuery<V> delegate) {
        this.core = core;
        this.delegate = delegate;
    }

    protected FDate adjustKey(final FDate key) {
        return core.getParent().adjustKey(key);
    }

    protected Iterable<FDate> adjustKey(final Iterable<FDate> keys) {
        return new Iterable<FDate>() {
            @Override
            public Iterator<FDate> iterator() {
                return new Iterator<FDate>() {
                    private final Iterator<FDate> iterator = keys.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public FDate next() {
                        final FDate next = iterator.next();
                        final FDate adj = adjustKey(next);
                        return adj;
                    }

                };
            }
        };
    }

    @Override
    public IHistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.withElementFilter(elementFilter);
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

    protected AdjustingHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return new AdjustingHistoricalCacheQueryWithFuture<V>(core, delegate.withFuture());
    }

    @Override
    public IHistoricalEntry<V> getEntry(final FDate key) {
        return delegate.getEntry(adjustKey(key));
    }

    @Override
    public V getValue(final FDate key) {
        return delegate.getValue(adjustKey(key));
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final Iterable<FDate> keys) {
        return delegate.getEntries(adjustKey(keys));
    }

    @Override
    public ICloseableIterable<V> getValues(final Iterable<FDate> keys) {
        return delegate.getValues(adjustKey(keys));
    }

    @Override
    public FDate getKey(final FDate key) {
        return delegate.getKey(adjustKey(key));
    }

    @Override
    public FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousKey(adjustKey(key), shiftBackUnits);
    }

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousKeys(adjustKey(key), shiftBackUnits);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousEntry(adjustKey(key), shiftBackUnits);
    }

    @Override
    public V getPreviousValue(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousValue(adjustKey(key), shiftBackUnits);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousEntries(adjustKey(key), shiftBackUnits);
    }

    @Override
    public ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
        return delegate.getPreviousValues(adjustKey(key), shiftBackUnits);
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getKeys(adjFrom, adjTo);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getEntries(adjFrom, adjTo);
    }

    @Override
    public ICloseableIterable<V> getValues(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getValues(adjFrom, adjTo);
    }

    @Override
    public FDate getPreviousKeyWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousKeyWithSameValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public V getPreviousValueWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousValueWithSameValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousEntryWithSameValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public FDate getPreviousKeyWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousKeyWithDifferentValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public V getPreviousValueWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousValueWithDifferentValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValueBetween(final FDate from, final FDate to,
            final V value) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return null;
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getPreviousEntryWithDifferentValueBetween(adjFrom, adjTo, value);
    }

    @Override
    public FDate getPreviousKeyWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        return delegate.getPreviousKeyWithSameValue(adjustKey(key), maxShiftBackUnits, value);
    }

    @Override
    public V getPreviousValueWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        return delegate.getPreviousValueWithSameValue(adjustKey(key), maxShiftBackUnits, value);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        return delegate.getPreviousEntryWithSameValue(adjustKey(key), maxShiftBackUnits, value);
    }

    @Override
    public FDate getPreviousKeyWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        return delegate.getPreviousKeyWithDifferentValue(adjustKey(key), maxShiftBackUnits, value);
    }

    @Override
    public V getPreviousValueWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        return delegate.getPreviousValueWithDifferentValue(adjustKey(key), maxShiftBackUnits, value);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        return delegate.getPreviousEntryWithDifferentValue(adjustKey(key), maxShiftBackUnits, value);
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
    public IHistoricalCacheQueryElementFilter<V> getElementFilter() {
        return delegate.getElementFilter();
    }

    @Override
    public void resetQuerySettings() {
        delegate.resetQuerySettings();
    }

    @Override
    public IHistoricalEntry<V> computeEntry(final FDate key) {
        return delegate.computeEntry(adjustKey(key));
    }

    @Override
    public FDate computeKey(final FDate key) {
        return delegate.computeKey(adjustKey(key));
    }

    @Override
    public V computeValue(final FDate key) {
        return delegate.computeValue(adjustKey(key));
    }

}
