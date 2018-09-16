package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class TrailingHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(CachedHistoricalCacheQueryCore.class);
    private static final int COUNT_RESETS_BEFORE_WARNING = 100;

    private final CachedHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("getParent().getLock()")
    private int countResets = 0;
    @GuardedBy("getParent().getLock()")
    private final List<Entry<FDate, V>> cachedPreviousEntries = new ArrayList<>();
    private volatile boolean cachedQueryActive;

    public TrailingHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new CachedHistoricalCacheQueryCore<V>(parent);
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return delegate.getParent();
    }

    @Override
    public Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        if (shiftBackUnits == 0) {
            return delegate.getPreviousEntry(query, key, 0);
        } else {
            //use arraylist since we don't want to have the overhead of filtering duplicates
            final boolean filterDuplicateKeys = false;
            final int incrementedShiftBackUnits = shiftBackUnits + 1;
            final List<Entry<FDate, V>> previousEntries = getPreviousEntriesList(query, key, incrementedShiftBackUnits,
                    filterDuplicateKeys);
            if (previousEntries.isEmpty()) {
                return null;
            } else {
                return previousEntries.get(0);
            }
        }
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final Entry<FDate, V> entry = delegate.getPreviousEntry(query, key, 0);
            return new SingleValueIterable<Entry<FDate, V>>(entry);
        } else {
            final List<Entry<FDate, V>> result = getPreviousEntriesList(query, key, shiftBackUnits,
                    query.isFilterDuplicateKeys());
            return WrapperCloseableIterable.maybeWrap(result);
        }
    }

    private List<Entry<FDate, V>> getPreviousEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) {
        synchronized (getParent().getLock()) {
            if (cachedQueryActive) {
                return getPreviousEntries_tooOldData(query, key, shiftBackUnits, filterDuplicateKeys);
            } else {
                cachedQueryActive = true;
                try {
                    try {
                        final List<Entry<FDate, V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                                shiftBackUnits, filterDuplicateKeys);
                        return result;
                    } catch (final ResetCacheException e) {
                        countResets++;
                        if (countResets % COUNT_RESETS_BEFORE_WARNING == 0
                                || AHistoricalCache.isDebugAutomaticReoptimization()) {
                            if (LOG.isWarnEnabled()) {
                                //CHECKSTYLE:OFF
                                LOG.warn(
                                        "{}: resetting {} for the {}. time now and retrying after exception [{}: {}], if this happens too often we might encounter bad performance due to inefficient caching",
                                        delegate.getParent(), getClass().getSimpleName(), countResets,
                                        e.getClass().getSimpleName(), e.getMessage());
                                //CHECKSTYLE:ON
                            }
                        }
                        resetForRetry();
                        try {
                            final List<Entry<FDate, V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                                    shiftBackUnits, filterDuplicateKeys);
                            return result;
                        } catch (final ResetCacheException e1) {
                            throw new RuntimeException("Follow up " + ResetCacheException.class.getSimpleName()
                                    + " on retry after:" + e.toString(), e1);
                        }
                    }
                } finally {
                    cachedQueryActive = false;
                }
            }
        }
    }

    private List<Entry<FDate, V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                    filterDuplicateKeys);
            cachedPreviousEntries.addAll(filterDuplicateKeys(result, filterDuplicateKeys));
            delegate.maybeIncreaseMaximumSize(cachedPreviousEntries.size());
            maybeTrimCachedPreviousEntries();
            return result;
        } else {
            final Entry<FDate, V> firstCachedEntry = getFirstCachedEntry();
            final Entry<FDate, V> lastCachedEntry = getLastCachedEntry();
            if (key.isBefore(firstCachedEntry.getKey())) {
                //a request that is way too old
                return getPreviousEntries_tooOldData(query, key, shiftBackUnits, filterDuplicateKeys);
            } else if (key.isAfter(lastCachedEntry.getKey())) {
                //a request that might lead to newer data that can be appended
                return getPreviousEntries_newerData(query, key, shiftBackUnits, filterDuplicateKeys, firstCachedEntry,
                        lastCachedEntry);
            } else {
                //somewhere in the middle
                //check units back and either fulfill directly
                //or fill as far as possible, do another request for remaining and then prepend more data as much as size allows
                final List<Entry<FDate, V>> trailing = delegate.newEntriesList(query, shiftBackUnits,
                        filterDuplicateKeys);
                final int newUnitsBack = fillFromCacheAsFarAsPossible(trailing, shiftBackUnits, key);
                if (newUnitsBack <= 0) {
                    return trailing;
                } else {
                    final Entry<FDate, V> fristValueFromCache = trailing.get(0);
                    final List<Entry<FDate, V>> remainingResult = delegate.getPreviousEntriesList(query,
                            fristValueFromCache.getKey(), newUnitsBack + 1, filterDuplicateKeys);
                    trailing.addAll(0, remainingResult.subList(0, remainingResult.size() - 1));
                    mergeResult(firstCachedEntry, lastCachedEntry, filterDuplicateKeys(trailing, filterDuplicateKeys));
                    return trailing;
                }
            }
        }
    }

    private List<Entry<FDate, V>> filterDuplicateKeys(final List<Entry<FDate, V>> result,
            final boolean filterDuplicateKeys) {
        if (filterDuplicateKeys) {
            return result;
        }
        int firstUniqueIndex = 0;
        Entry<FDate, V> prevEntry = null;
        for (int i = 0; i < result.size(); i++) {
            final Entry<FDate, V> entry = result.get(i);
            if (prevEntry != null && prevEntry.getKey().isBefore(entry.getKey())) {
                break;
            }
            firstUniqueIndex = i;
            prevEntry = entry;
        }
        if (firstUniqueIndex == 0) {
            return result;
        } else {
            return result.subList(firstUniqueIndex, result.size());
        }
    }

    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) throws ResetCacheException {
        //prefill what is possible and add suffixes by query as needed
        final int cachedToIndex;
        if (skippingKeysAbove != null) {
            cachedToIndex = delegate.bisect(skippingKeysAbove, cachedPreviousEntries, unitsBack);
        } else {
            cachedToIndex = cachedPreviousEntries.size() - 1;
        }

        final int toIndex = cachedToIndex + 1;
        final int fromIndex = Math.max(0, toIndex - unitsBack);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;
        trailing.addAll(cachedPreviousEntries.subList(fromIndex, toIndex));

        return newUnitsBack;
    }

    private List<Entry<FDate, V>> getPreviousEntries_newerData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys,
            final Entry<FDate, V> firstCachedEntry, final Entry<FDate, V> lastCachedEntry) throws ResetCacheException {
        final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        mergeResult(firstCachedEntry, lastCachedEntry, filterDuplicateKeys(result, filterDuplicateKeys));
        return result;
    }

    private void mergeResult(final Entry<FDate, V> firstCachedEntry, final Entry<FDate, V> lastCachedEntry,
            final List<Entry<FDate, V>> result) throws ResetCacheException {
        final Entry<FDate, V> firstResultEntry = result.get(0);
        final Entry<FDate, V> lastResultEntry = result.get(0);
        if (firstResultEntry.getKey().isAfter(lastCachedEntry.getKey())) {
            //first result is after last cached entry, replace the cached entries
            cachedPreviousEntries.clear();
            cachedPreviousEntries.addAll(result);
            delegate.maybeIncreaseMaximumSize(cachedPreviousEntries.size());
            //finished
            return;
        } else if (lastResultEntry.getKey().isBefore(firstCachedEntry.getKey())) {
            //last result is before first cached entry, nothing to merge
            return;
        }

        if (lastResultEntry.getKey().isAfter(lastCachedEntry.getKey())) {
            //we have some data that can be merged at the end of the result
            final int fromIndex = delegate.bisect(lastCachedEntry.getKey(), result, null) + 1;
            final int toIndex = result.size();
            final List<Entry<FDate, V>> sublist = result.subList(fromIndex, toIndex);
            cachedPreviousEntries.addAll(sublist);
            delegate.maybeIncreaseMaximumSize(cachedPreviousEntries.size());
            maybeTrimCachedPreviousEntries();
        }

        final Integer maximumSize = getParent().getMaximumSize();
        if ((maximumSize == null || cachedPreviousEntries.size() < maximumSize)
                && firstResultEntry.getKey().isBefore(firstCachedEntry.getKey())) {
            //we have some data that can be merged at the start of the result
            int fromIndex = 0;
            final int toIndex = delegate.bisect(lastCachedEntry.getKey(), result, null);
            if (maximumSize != null) {
                int maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - maximumSize;
                delegate.maybeIncreaseMaximumSize(cachedPreviousEntries.size() + maximumSizeExceededBy);
                maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - getParent().getMaximumSize();
                if (maximumSizeExceededBy > 0) {
                    fromIndex = maximumSizeExceededBy;
                }
            }
            final List<Entry<FDate, V>> sublist = result.subList(fromIndex, toIndex);
            cachedPreviousEntries.addAll(0, sublist);
        }
    }

    private void maybeTrimCachedPreviousEntries() {
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            //trim values that are too much
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(0);
            }
        }
    }

    private List<Entry<FDate, V>> getPreviousEntries_tooOldData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) {
        final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        return result;
    }

    private Entry<FDate, V> getLastCachedEntry() throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty()) {
            throw new ResetCacheException("lastCachedEntry cannot be retrieved since cachedPreviousEntries is empty");
        }
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
    }

    private Entry<FDate, V> getFirstCachedEntry() {
        return cachedPreviousEntries.get(0);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntries(query, key, shiftForwardUnits);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return delegate.getNextEntry(query, key, shiftForwardUnits);
    }

    @Override
    public void clear() {
        if (!cachedQueryActive) {
            synchronized (getParent().getLock()) {
                resetForRetry();
                countResets = 0;
            }
        }
    }

    private void resetForRetry() {
        delegate.clear();
        cachedPreviousEntries.clear();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        delegate.increaseMaximumSize(maximumSize);
    }

    @Override
    public V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getValue(query, key, assertValue);
    }

    @Override
    public Entry<FDate, V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getEntry(query, key, assertValue);
    }

    @Override
    public Entry<FDate, V> computeEntry(final HistoricalCacheQuery<V> historicalCacheQuery, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.computeEntry(historicalCacheQuery, key, assertValue);
    }

    @Override
    public void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
        synchronized (getParent().getLock()) {
            if (cachedQueryActive) {
                return;
            }
            delegate.putPrevious(previousKey, value, valueKey);
            try {
                if (!cachedPreviousEntries.isEmpty()) {
                    final Entry<FDate, V> lastEntry = getLastCachedEntry();
                    if (lastEntry.getKey().isBefore(previousKey)) {
                        cachedPreviousEntries.clear();
                    } else if (lastEntry.getKey().isAfter(previousKey)) {
                        return;
                    }
                }
                cachedPreviousEntries.add(ImmutableEntry.of(valueKey, value));
                maybeTrimCachedPreviousEntries();
            } catch (final ResetCacheException e) {
                //should not happen here
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void putPreviousKey(final FDate previousKey, final FDate valueKey) {
        synchronized (getParent().getLock()) {
            if (cachedQueryActive) {
                return;
            }
            if (cachedPreviousEntries.isEmpty()) {
                final V newValue = getParent().computeValue(valueKey);
                putPrevious(previousKey, newValue, valueKey);
                return;
            }
            try {
                final Entry<FDate, V> lastEntry = getLastCachedEntry();
                if (!lastEntry.getKey().equals(previousKey)) {
                    if (lastEntry.getKey().isBefore(previousKey)) {
                        final V newValue = getParent().computeValue(valueKey);
                        putPrevious(previousKey, newValue, valueKey);
                    }
                    return;
                }
                final V newValue = getParent().computeValue(valueKey);
                final Entry<FDate, V> newEntry = ImmutableEntry.of(valueKey, newValue);
                getParent().getPutProvider().put(newEntry, lastEntry);
            } catch (final ResetCacheException e) {
                //should not happen here
                throw new RuntimeException(e);
            }
        }
    }

}
