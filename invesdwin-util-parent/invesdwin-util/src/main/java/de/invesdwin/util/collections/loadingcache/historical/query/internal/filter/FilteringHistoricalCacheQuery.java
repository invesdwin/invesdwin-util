package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.key.AdjustedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class FilteringHistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private final IHistoricalCacheInternalMethods<V> internalMethods;
    private final IHistoricalCacheQuery<V> delegate;

    public FilteringHistoricalCacheQuery(final IHistoricalCacheInternalMethods<V> internalMethods,
            final IHistoricalCacheQuery<V> delegate) {
        this.internalMethods = internalMethods;
        this.delegate = delegate;
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

    protected FilteringHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return new FilteringHistoricalCacheQueryWithFuture<V>(internalMethods, delegate.withFuture());
    }

    @Override
    public IEvaluateGenericFDate<IHistoricalEntry<V>> newGetEntry() {
        final IEvaluateGenericFDate<IHistoricalEntry<V>> getEntryF = delegate.newGetEntry();
        return pKey -> {
            final FDate key = pKey.asFDate();
            if (!(key instanceof AdjustedFDate) && !(key instanceof IndexedFDate)) {
                final V latestValue = internalMethods.getHighestAllowedValueInterceptor(key);
                if (latestValue != null) {
                    return ImmutableHistoricalEntry.maybeExtractKey(internalMethods.getParent(), null, latestValue);
                }
            }
            return getEntryF.evaluateGeneric(key);
        };
    }

    @Override
    public IEvaluateGenericFDate<FDate> newGetKey() {
        return delegate.newGetKey();
    }

    @Override
    public IEvaluateGenericFDate<V> newGetValue() {
        final IEvaluateGenericFDate<V> getValueF = delegate.newGetValue();
        return pKey -> {
            final FDate key = pKey.asFDate();
            if (!(key instanceof AdjustedFDate) && !(key instanceof IndexedFDate)) {
                final V latestValue = internalMethods.getHighestAllowedValueInterceptor(key);
                if (latestValue != null) {
                    return latestValue;
                }
            }
            return getValueF.evaluateGeneric(key);
        };
    }

    @Override
    public IHistoricalEntry<V> getEntry(final FDate key) {
        if (!(key instanceof AdjustedFDate) && !(key instanceof IndexedFDate)) {
            final V latestValue = internalMethods.getHighestAllowedValueInterceptor(key);
            if (latestValue != null) {
                return ImmutableHistoricalEntry.maybeExtractKey(internalMethods.getParent(), null, latestValue);
            }
        }
        return delegate.getEntry(key);
    }

    @Override
    public IHistoricalEntry<V> getEntryIfPresent(final FDate key) {
        if (!(key instanceof AdjustedFDate) && !(key instanceof IndexedFDate)) {
            final V latestValue = internalMethods.getHighestAllowedValueInterceptor(key);
            if (latestValue != null) {
                return ImmutableHistoricalEntry.maybeExtractKey(internalMethods.getParent(), null, latestValue);
            }
        }
        return delegate.getEntryIfPresent(key);
    }

    @Override
    public V getValue(final FDate key) {
        if (!(key instanceof AdjustedFDate) && !(key instanceof IndexedFDate)) {
            final V latestValue = internalMethods.getHighestAllowedValueInterceptor(key);
            if (latestValue != null) {
                return latestValue;
            }
        }
        return delegate.getValue(key);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final Iterable<FDate> keys) {
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
        final FDate result = delegate.getPreviousKey(key, shiftBackUnits);
        if (result == null || result.isAfter(key)) {
            //prevent nullpointers
            return key;
        } else {
            return result;
        }
    }

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        /*
         * need to go directly against getKey so that recursive queries work properly with shift keys delegates
         */
        final ICloseableIterable<FDate> result = delegate.getPreviousKeys(key, shiftBackUnits);
        return new AFilterSkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isAfter(key);
            }
        };
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        final IHistoricalEntry<V> result = delegate.getPreviousEntry(key, shiftBackUnits);
        if (result == null || result.getKey().isAfter(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getPreviousValue(final FDate key, final int shiftBackUnits) {
        final IHistoricalEntry<V> result = getPreviousEntry(key, shiftBackUnits);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        final ICloseableIterable<IHistoricalEntry<V>> result = delegate.getPreviousEntries(key, shiftBackUnits);
        return new AFilterSkippingIterable<IHistoricalEntry<V>>(result) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                return element.getKey().isAfter(key);
            }
        };
    }

    @Override
    public ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = getPreviousEntries(key, shiftBackUnits);
                        entriesIterator = entries.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }

                };
            }
        };
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return new AFilterSkippingIterable<FDate>(delegate.getKeys(from, to)) {
            @Override
            protected boolean skip(final FDate element) {
                if (element.isAfter(to)) {
                    return true;
                }
                if (element.isBefore(from)) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final FDate from, final FDate to) {
        return new AFilterSkippingIterable<IHistoricalEntry<V>>(delegate.getEntries(from, to)) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                if (element.getKey().isAfter(to)) {
                    return true;
                }
                if (element.getKey().isBefore(from)) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public ICloseableIterable<V> getValues(final FDate from, final FDate to) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = getEntries(from, to);
                        entriesIterator = entries.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }

                };
            }
        };
    }

    @Override
    public ICloseableIterable<FDate> getKeysCached(final FDate from, final FDate to) {
        return new AFilterSkippingIterable<FDate>(delegate.getKeysCached(from, to)) {
            @Override
            protected boolean skip(final FDate element) {
                if (element.isAfter(to)) {
                    return true;
                }
                if (element.isBefore(from)) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntriesCached(final FDate from, final FDate to) {
        return new AFilterSkippingIterable<IHistoricalEntry<V>>(delegate.getEntriesCached(from, to)) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                if (element.getKey().isAfter(to)) {
                    return true;
                }
                if (element.getKey().isBefore(from)) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public ICloseableIterable<V> getValuesCached(final FDate from, final FDate to) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = getEntriesCached(from, to);
                        entriesIterator = entries.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }

                };
            }
        };
    }

    @Override
    public FDate getPreviousKeyWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final FDate result = delegate.getPreviousKeyWithSameValueBetween(from, to, value);
        if (result == null || (result.isBefore(from) || result.isAfter(to))) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getPreviousValueWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> result = getPreviousEntryWithSameValueBetween(from, to, value);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> result = delegate.getPreviousEntryWithSameValueBetween(from, to, value);
        if (result == null || (result.getKey().isBefore(from) || result.getKey().isAfter(to))) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final FDate result = delegate.getPreviousKeyWithDifferentValueBetween(from, to, value);
        if (result == null || (result.isBefore(from) || result.isAfter(to))) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getPreviousValueWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> result = getPreviousEntryWithDifferentValueBetween(from, to, value);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValueBetween(final FDate from, final FDate to,
            final V value) {
        final IHistoricalEntry<V> result = delegate.getPreviousEntryWithDifferentValueBetween(from, to, value);
        if (result == null || (result.getKey().isBefore(from) || result.getKey().isAfter(to))) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public FDate getPreviousKeyWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final FDate result = delegate.getPreviousKeyWithSameValue(key, maxShiftBackUnits, value);
        if (result == null || result.isAfter(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getPreviousValueWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> result = getPreviousEntryWithSameValue(key, maxShiftBackUnits, value);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        final IHistoricalEntry<V> result = delegate.getPreviousEntryWithSameValue(key, maxShiftBackUnits, value);
        if (result == null || result.getKey().isAfter(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final FDate result = delegate.getPreviousKeyWithDifferentValue(key, maxShiftBackUnits, value);
        if (result == null || result.isAfter(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getPreviousValueWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> result = getPreviousEntryWithDifferentValue(key, maxShiftBackUnits, value);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        final IHistoricalEntry<V> result = delegate.getPreviousEntryWithDifferentValue(key, maxShiftBackUnits, value);
        if (result == null || result.getKey().isAfter(key)) {
            return null;
        } else {
            return result;
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
    public IHistoricalCacheQueryElementFilter<V> getElementFilter() {
        return delegate.getElementFilter();
    }

    @Override
    public void resetQuerySettings() {
        delegate.resetQuerySettings();
    }

    @Override
    public IHistoricalEntry<V> computeEntry(final FDate key) {
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
