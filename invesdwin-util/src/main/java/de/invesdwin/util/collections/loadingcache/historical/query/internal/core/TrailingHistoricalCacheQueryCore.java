package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class TrailingHistoricalCacheQueryCore<V> extends ACachedHistoricalCacheQueryCore<V> {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(CachedHistoricalCacheQueryCore.class);
    private static final int COUNT_RESETS_BEFORE_WARNING = 100;

    private final CachedHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("getParent().getLock()")
    private int countResets = 0;
    private volatile boolean cachedQueryActive;

    public TrailingHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new CachedHistoricalCacheQueryCore<V>(parent);
    }

    @Override
    protected IHistoricalCacheQueryCore<V> getDelegate() {
        return delegate;
    }

    @Override
    protected List<Entry<FDate, V>> getPreviousEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
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

    @Override
    protected Integer maybeIncreaseMaximumSize(final int requiredSize) {
        return delegate.maybeIncreaseMaximumSize(requiredSize);
    }

    private List<Entry<FDate, V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) throws ResetCacheException {
        final List<Entry<FDate, V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, key, shiftBackUnits, filterDuplicateKeys);
        } else {
            result = defaultGetPreviousEntries(query, key, shiftBackUnits, filterDuplicateKeys);
            updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
        }
        return result;
    }

    private List<Entry<FDate, V>> defaultGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) {
        final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
        replaceCachedEntries(key, result);
        return result;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) throws ResetCacheException {
        final Entry<IndexedFDate, V> firstCachedEntry = getFirstCachedEntry();
        final Entry<IndexedFDate, V> lastCachedEntry = getLastCachedEntry();
        if (key.equalsNotNullSafe(cachedPreviousEntriesKey) || key.equalsNotNullSafe(lastCachedEntry.getKey())) {
            final List<Entry<FDate, V>> tryCachedPreviousResult = tryCachedPreviousResult_sameKey(query, shiftBackUnits,
                    filterDuplicateKeys);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                if (cachedPreviousResult_shiftBackUnits != null) {
                    resetCachedPreviousResult();
                }
                final List<Entry<FDate, V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(query, key,
                        shiftBackUnits, filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
                updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
                return result;
            }
        } else if (key.isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //a request that is way too old
            return getPreviousEntries_tooOldData(query, key, shiftBackUnits, filterDuplicateKeys);
        } else if (key.isAfterNotNullSafe(cachedPreviousEntriesKey)
                || key.isAfterNotNullSafe(lastCachedEntry.getKey())) {
            //a request that might lead to newer data that can be appended
            if (cachedPreviousResult_shiftBackUnits != null) {
                resetCachedPreviousResult();
            }
            final List<Entry<FDate, V>> result = getPreviousEntries_newerData(query, key, shiftBackUnits,
                    filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
            updateCachedPreviousResult(query, shiftBackUnits, result, filterDuplicateKeys);
            return result;
        } else {
            //somewhere in the middle
            //check units back and either fulfill directly
            //or fill as far as possible, do another request for remaining and then prepend more data as much as size allows
            final List<Entry<FDate, V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(query, key,
                    shiftBackUnits, filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
            //like decremented key, not updating cached previous result
            return result;
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_somewhereInTheMiddle(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys, final Entry<IndexedFDate, V> firstCachedEntry,
            final Entry<IndexedFDate, V> lastCachedEntry) throws ResetCacheException {
        final List<Entry<FDate, V>> trailing = delegate.newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
        final int newUnitsBack = fillFromCacheAsFarAsPossible(trailing, shiftBackUnits, key);
        if (newUnitsBack <= 0) {
            return trailing;
        } else {
            final Entry<FDate, V> fristValueFromCache = trailing.get(0);
            final List<Entry<FDate, V>> remainingResult = delegate.getPreviousEntriesList(query,
                    fristValueFromCache.getKey(), newUnitsBack + 1, filterDuplicateKeys);
            trailing.addAll(0, remainingResult.subList(0, remainingResult.size() - 1));
            mergeResult(key, firstCachedEntry, lastCachedEntry, trailing);
            return trailing;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) throws ResetCacheException {
        //prefill what is possible and add suffixes by query as needed
        final int cachedToIndex;
        if (skippingKeysAbove != null) {
            cachedToIndex = bisect(skippingKeysAbove, (List) cachedPreviousEntries, unitsBack, this);
        } else {
            cachedToIndex = cachedPreviousEntries.size() - 1;
        }

        final int toIndex = cachedToIndex + 1;
        final int fromIndex = Math.max(0, toIndex - unitsBack);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;
        trailing.addAll((List) cachedPreviousEntries.subList(fromIndex, toIndex));

        return newUnitsBack;
    }

    @Override
    protected int bisect(final FDate skippingKeysAbove, final List<Entry<FDate, V>> list, final Integer unitsBack,
            final ACachedHistoricalCacheQueryCore<V> useIndex) throws ResetCacheException {
        return delegate.bisect(skippingKeysAbove, list, unitsBack, useIndex);
    }

    private void prependCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInCache) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInCache - 1; i >= 0; i--) {
            final Entry<FDate, V> prependEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final Entry<IndexedFDate, V> firstCachedEntry = getFirstCachedEntry();
                if (!prependEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
                    throw new IllegalStateException("prependEntry [" + prependEntry.getKey()
                            + "] should be before firstCachedEntry [" + firstCachedEntry.getKey() + "]");
                }
            }
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(prependEntry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(modCount, 0 - modIncrementIndex - 1));
            final Entry<IndexedFDate, V> indexedEntry = ImmutableEntry.of(indexedKey, prependEntry.getValue());
            cachedPreviousEntries.add(0, indexedEntry);
            modIncrementIndex++;
            assertIndexes();
        }
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null && cachedPreviousEntries.size() > maximumSize) {
            throw new IllegalStateException("maximumSize [" + maximumSize
                    + "] was exceeded during prependCachedEntries: " + cachedPreviousEntries.size());
        }
    }

    private void appendCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInQuery) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInQuery; i < trailing.size(); i++) {
            final Entry<FDate, V> appendEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final Entry<IndexedFDate, V> lastCachedEntry = getLastCachedEntry();
                if (!appendEntry.getKey().isAfterNotNullSafe(lastCachedEntry.getKey())) {
                    throw new ResetCacheException("appendEntry [" + appendEntry.getKey()
                            + "] should be after lastCachedEntry [" + lastCachedEntry.getKey() + "]");
                }
            }
            if (cachedPreviousResult_shiftBackUnits != null) {
                appendCachedEntryAndResult(key, cachedPreviousResult_shiftBackUnits, appendEntry);
            } else {
                final IndexedFDate indexedKey = IndexedFDate.maybeWrap(appendEntry.getKey());
                indexedKey.putQueryCoreIndex(this,
                        new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
                final Entry<IndexedFDate, V> indexedEntry = ImmutableEntry.of(indexedKey, appendEntry.getValue());
                cachedPreviousEntries.add(indexedEntry);
            }
        }
        if (cachedPreviousResult_shiftBackUnits == null) {
            cachedPreviousEntriesKey = key;
            Integer maximumSize = getParent().getMaximumSize();
            if (maximumSize != null) {
                maximumSize = maybeIncreaseMaximumSize(trailing.size());
                //ensure we stay in size limit
                while (cachedPreviousEntries.size() > maximumSize) {
                    cachedPreviousEntries.remove(0);
                    modIncrementIndex--;
                    assertIndexes();
                }
            }
        }
    }

    private List<Entry<FDate, V>> getPreviousEntries_newerData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys,
            final Entry<IndexedFDate, V> firstCachedEntry, final Entry<IndexedFDate, V> lastCachedEntry)
            throws ResetCacheException {
        final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        mergeResult(key, firstCachedEntry, lastCachedEntry, result);
        return result;
    }

    private void mergeResult(final FDate key, final Entry<IndexedFDate, V> firstCachedEntry,
            final Entry<IndexedFDate, V> lastCachedEntry, final List<Entry<FDate, V>> result)
            throws ResetCacheException {
        final Entry<FDate, V> firstResultEntry = result.get(0);
        final Entry<FDate, V> lastResultEntry = result.get(result.size() - 1);
        if (firstResultEntry.getKey().isAfterNotNullSafe(lastCachedEntry.getKey())) {
            //first result is after last cached entry, replace the cached entries
            //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
            replaceCachedEntries(key, result);
            //finished
            return;
        } else if (lastResultEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //last result is before first cached entry, nothing to merge
            return;
        }

        if (lastResultEntry.getKey().isAfterNotNullSafe(lastCachedEntry.getKey())) {
            //we have some data that can be merged at the end of the result
            final int fromIndex = delegate.bisect(lastCachedEntry.getKey(), result, null, null) + 1;
            final int toIndex = result.size();
            final int sizeToAppend = toIndex - fromIndex;
            appendCachedEntries(key, result, sizeToAppend);
        }

        Integer maximumSize = getParent().getMaximumSize();
        if ((maximumSize == null || cachedPreviousEntries.size() < maximumSize)
                && firstResultEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //we have some data that can be merged at the start of the result
            int fromIndex = 0;
            final int toIndex = delegate.bisect(firstCachedEntry.getKey(), result, null, null);
            if (maximumSize != null) {
                int maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - maximumSize - 1;
                maximumSize = maybeIncreaseMaximumSize(cachedPreviousEntries.size() + maximumSizeExceededBy);
                maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - maximumSize;
                if (maximumSizeExceededBy > 0) {
                    fromIndex = maximumSizeExceededBy;
                }
            }
            final int sizeToPrepend = toIndex - fromIndex;
            final int trailingFoundInCache = result.size() - sizeToPrepend;
            prependCachedEntries(key, result, trailingFoundInCache);
        }
    }

    private List<Entry<FDate, V>> getPreviousEntries_tooOldData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys) {
        final List<Entry<FDate, V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        return result;
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

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        delegate.increaseMaximumSize(maximumSize);
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
                    final Entry<IndexedFDate, V> lastEntry = getLastCachedEntry();
                    if (lastEntry.getKey().isBeforeNotNullSafe(previousKey)) {
                        //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
                        replaceCachedEntries(valueKey, Arrays.asList(ImmutableEntry.of(valueKey, value)));
                        return;
                    } else if (lastEntry.getKey().isAfterNotNullSafe(previousKey)) {
                        return;
                    }
                }
                appendCachedEntryAndResult(valueKey, null, ImmutableEntry.of(valueKey, value));
            } catch (final ResetCacheException e) {
                //should not happen here
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
                final Entry<IndexedFDate, V> lastEntry = getLastCachedEntry();
                if (!lastEntry.getKey().equalsNotNullSafe(previousKey)) {
                    if (lastEntry.getKey().isBeforeNotNullSafe(previousKey)) {
                        final V newValue = getParent().computeValue(valueKey);
                        putPrevious(previousKey, newValue, valueKey);
                    }
                    return;
                }
                final V newValue = getParent().computeValue(valueKey);
                final Entry<FDate, V> newEntry = ImmutableEntry.of(valueKey, newValue);
                getParent().getPutProvider().put(newEntry, (Entry) lastEntry, true);
            } catch (final ResetCacheException e) {
                //should not happen here
                throw new RuntimeException(e);
            }
        }
    }

}
