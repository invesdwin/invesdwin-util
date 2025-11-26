package de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class AdjustingHistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private final IHistoricalCacheInternalMethods<V> internalMethods;
    private final IHistoricalCacheQuery<V> delegate;

    public AdjustingHistoricalCacheQuery(final IHistoricalCacheInternalMethods<V> internalMethods) {
        this(internalMethods, new HistoricalCacheQuery<V>(internalMethods));
    }

    protected AdjustingHistoricalCacheQuery(final IHistoricalCacheInternalMethods<V> internalMethods,
            final IHistoricalCacheQuery<V> delegate) {
        this.internalMethods = internalMethods;
        this.delegate = delegate;
    }

    protected FDate alignAndAdjustKey(final FDate key) {
        if (internalMethods.isAlignKeys()) {
            if (!internalMethods.isAdjustedKey(key)) {
                //align to reduce cache misses (which are very expensive in recursive queries)
                return getKey(key);
            } else {
                return key;
            }
        } else {
            return adjustKey(key);
        }
    }

    protected FDate adjustKey(final FDate key) {
        return internalMethods.adjustKey(key);
    }

    protected boolean isFutureEntry(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return false;
        }
        return isFutureKey(entry.getKey());
    }

    protected boolean isFutureKey(final FDate key) {
        if (key == null) {
            return false;
        }
        if (key.isAfter(internalMethods.getHighestAllowedKey(false))) {
            return true;
        }
        return false;
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
    public IEvaluateGenericFDate<IHistoricalEntry<V>> newGetEntry() {
        final IEvaluateGenericFDate<IHistoricalEntry<V>> getEntryF = delegate.newGetEntry();
        return (key) -> getEntryF.evaluateGeneric(() -> alignAndAdjustKey(key.asFDate()));
    }

    @Override
    public IEvaluateGenericFDate<FDate> newGetKey() {
        final IEvaluateGenericFDate<FDate> getKeyF = delegate.newGetKey();
        return pKey -> {
            final IHistoricalCacheAdjustKeyProvider adjustKeyProvider = internalMethods.getAdjustKeyProvider();
            if (adjustKeyProvider.isAlreadyAdjustingKey()) {
                return getKeyF.evaluateGeneric(pKey);
            }
            final FDate highestAllowedKey = adjustKeyProvider.getHighestAllowedKey(false);
            if (highestAllowedKey == null) {
                return getKeyF.evaluateGeneric(() -> adjustKey(pKey.asFDate()));
            }
            final FDate key = pKey.asFDate();
            if (key.isAfterOrEqualToNotNullSafe(highestAllowedKey)) {
                //we trust that the highest allowed key is the correct key of the last available entry
                return highestAllowedKey;
            } else {
                final FDate prevHighestAllowedKey = adjustKeyProvider.getPreviousHighestAllowedKey();
                if (prevHighestAllowedKey != null && key.isAfterOrEqualToNotNullSafe(prevHighestAllowedKey)) {
                    //we trust that the highest allowed key is the correct key of the last available entry
                    return prevHighestAllowedKey;
                } else {
                    return getKeyF.evaluateGeneric(() -> adjustKey(pKey.asFDate()));
                }
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<V> newGetValue() {
        final IEvaluateGenericFDate<V> getValueF = delegate.newGetValue();
        return (key) -> getValueF.evaluateGeneric(() -> alignAndAdjustKey(key.asFDate()));
    }

    @Override
    public IHistoricalCacheQuery<V> setElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.setElementFilter(elementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> setFutureNullEnabled() {
        Assertions.checkSame(delegate.setFutureNullEnabled(), delegate);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> setFutureEnabled() {
        return newFutureQuery();
    }

    protected AdjustingHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return new AdjustingHistoricalCacheQueryWithFuture<V>(internalMethods, delegate.setFutureEnabled());
    }

    @Override
    public IHistoricalEntry<V> getEntry(final FDate key) {
        return delegate.getEntry(alignAndAdjustKey(key));
    }

    @Override
    public IHistoricalEntry<V> getEntryIfPresent(final FDate key) {
        return delegate.getEntryIfPresent(alignAndAdjustKey(key));
    }

    @Override
    public V getValue(final FDate key) {
        return delegate.getValue(alignAndAdjustKey(key));
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
        if (key == null) {
            return null;
        }
        final IHistoricalCacheAdjustKeyProvider adjustKeyProvider = internalMethods.getAdjustKeyProvider();
        if (adjustKeyProvider.isAlreadyAdjustingKey()) {
            return delegate.getKey(key);
        }
        final FDate highestAllowedKey = adjustKeyProvider.getHighestAllowedKey(false);
        if (highestAllowedKey == null) {
            return delegate.getKey(adjustKey(key));
        } else if (key.isAfterOrEqualToNotNullSafe(highestAllowedKey)) {
            //we trust that the highest allowed key is the correct key of the last available entry
            return highestAllowedKey;
        } else {
            final FDate prevHighestAllowedKey = adjustKeyProvider.getPreviousHighestAllowedKey();
            if (prevHighestAllowedKey != null && key.isAfterOrEqualToNotNullSafe(prevHighestAllowedKey)) {
                //we trust that the highest allowed key is the correct key of the last available entry
                return prevHighestAllowedKey;
            } else {
                return delegate.getKey(adjustKey(key));
            }
        }
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
    public ICloseableIterable<FDate> getKeysCached(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getKeysCached(adjFrom, adjTo);
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getEntriesCached(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getEntriesCached(adjFrom, adjTo);
    }

    @Override
    public ICloseableIterable<V> getValuesCached(final FDate from, final FDate to) {
        final FDate adjFrom = adjustKey(from);
        if (adjFrom != null && adjFrom.isBefore(from)) {
            return EmptyCloseableIterable.getInstance();
        }
        final FDate adjTo = adjustKey(to);
        return delegate.getValuesCached(adjFrom, adjTo);
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
        return delegate.computeEntry(alignAndAdjustKey(key));
    }

    @Override
    public FDate computeKey(final FDate key) {
        return delegate.computeKey(alignAndAdjustKey(key));
    }

    @Override
    public V computeValue(final FDate key) {
        return delegate.computeValue(alignAndAdjustKey(key));
    }

}
