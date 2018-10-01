package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.FlatteningIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class TrailingHistoricalCacheQueryCore<V> extends ACachedEntriesHistoricalCacheQueryCore<V> {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(CachedHistoricalCacheQueryCore.class);
    private static final int COUNT_RESETS_BEFORE_WARNING = 100;

    private final CachedHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("cachedQueryActiveLock")
    private int countResets = 0;
    private final ILock cachedQueryActiveLock = Locks
            .newReentrantLock(getClass().getSimpleName() + "_cachedQueryActiveLock");
    @GuardedBy("cachedQueryActiveLock")
    private boolean cachedQueryActive = false;

    public TrailingHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new CachedHistoricalCacheQueryCore<V>(parent);
    }

    @Override
    protected IHistoricalCacheQueryCore<V> getDelegate() {
        return delegate;
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        if (shiftBackUnits == 0) {
            return getDelegate().getPreviousEntry(query, key, 0);
        } else {
            //use arraylist since we don't want to have the overhead of filtering duplicates
            final boolean filterDuplicateKeys = false;
            final int incrementedShiftBackUnits = shiftBackUnits + 1;
            try (ICloseableIterator<IHistoricalEntry<V>> previousEntries = getPreviousEntriesList(query, key,
                    incrementedShiftBackUnits, filterDuplicateKeys).iterator()) {
                final IHistoricalEntry<V> next = previousEntries.next();
                return next;
            } catch (final NoSuchElementException e) {
                return null;
            }
        }
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final IHistoricalEntry<V> entry = getDelegate().getPreviousEntry(query, key, 0);
            return new SingleValueIterable<IHistoricalEntry<V>>(entry);
        } else {
            final ICloseableIterable<IHistoricalEntry<V>> result = getPreviousEntriesList(query, key, shiftBackUnits,
                    query.isFilterDuplicateKeys());
            return result;
        }
    }

    private ICloseableIterable<IHistoricalEntry<V>> getPreviousEntriesList(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) {
        final boolean cachedQueryActiveLocked = cachedQueryActiveLock.tryLock();
        if (!cachedQueryActiveLocked || cachedQueryActive) {
            try {
                return getPreviousEntries_tooOldData(query, key, shiftBackUnits, filterDuplicateKeys);
            } finally {
                if (cachedQueryActiveLocked) {
                    cachedQueryActiveLock.unlock();
                }
            }
        } else {
            try {
                cachedQueryActive = true;
                try {
                    final ICloseableIterable<IHistoricalEntry<V>> result = tryCachedGetPreviousEntriesIfAvailable(query,
                            key, shiftBackUnits, filterDuplicateKeys);
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
                        final ICloseableIterable<IHistoricalEntry<V>> result = tryCachedGetPreviousEntriesIfAvailable(
                                query, key, shiftBackUnits, filterDuplicateKeys);
                        return result;
                    } catch (final ResetCacheException e1) {
                        throw new RuntimeException("Follow up " + ResetCacheException.class.getSimpleName()
                                + " on retry after:" + e.toString(), e1);
                    }
                }
            } finally {
                cachedQueryActive = false;
                cachedQueryActiveLock.unlock();
            }
        }
    }

    @Override
    protected Integer maybeIncreaseMaximumSize(final int requiredSize) {
        return delegate.maybeIncreaseMaximumSize(requiredSize);
    }

    private ICloseableIterable<IHistoricalEntry<V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) throws ResetCacheException {
        final ICloseableIterable<IHistoricalEntry<V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, key, shiftBackUnits, filterDuplicateKeys);
        } else {
            result = defaultGetPreviousEntries(query, key, shiftBackUnits, filterDuplicateKeys);
        }
        return result;
    }

    private ICloseableIterable<IHistoricalEntry<V>> defaultGetPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
        replaceCachedEntries(key, result);
        return WrapperCloseableIterable.maybeWrap(result);
    }

    private ICloseableIterable<IHistoricalEntry<V>> cachedGetPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) throws ResetCacheException {
        final IHistoricalEntry<V> firstCachedEntry = getFirstCachedEntry();
        final IHistoricalEntry<V> lastCachedEntry = getLastCachedEntry();
        if (key.equalsNotNullSafe(lastCachedEntry.getKey())) {
            final ICloseableIterable<IHistoricalEntry<V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(query,
                    key, shiftBackUnits, filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
            return result;
        } else if (key.isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //a request that is way too old
            final ICloseableIterable<IHistoricalEntry<V>> result = getPreviousEntries_tooOldData(query, key,
                    shiftBackUnits, filterDuplicateKeys);
            return result;
        } else if (key.isAfterNotNullSafe(lastCachedEntry.getKey())) {
            //a request that might lead to newer data that can be appended
            final List<IHistoricalEntry<V>> result = getPreviousEntries_newerData(query, key, shiftBackUnits,
                    filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
            return WrapperCloseableIterable.maybeWrap(result);
        } else {
            //somewhere in the middle
            //check units back and either fulfill directly
            //or fill as far as possible, do another request for remaining and then prepend more data as much as size allows
            final ICloseableIterable<IHistoricalEntry<V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(query,
                    key, shiftBackUnits, filterDuplicateKeys, firstCachedEntry, lastCachedEntry);
            //like decremented key, not updating cached previous result
            return result;
        }
    }

    private ICloseableIterable<IHistoricalEntry<V>> cachedGetPreviousEntries_somewhereInTheMiddle(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys, final IHistoricalEntry<V> firstCachedEntry,
            final IHistoricalEntry<V> lastCachedEntry) throws ResetCacheException {
        final CachedEntriesSubListIterable cachedIterable = fillFromCacheAsFarAsPossible(shiftBackUnits, key);
        final int newUnitsBack = cachedIterable.getNewUnitsBack();
        if (newUnitsBack <= 0) {
            return maybeFilterDuplicateKeys(cachedIterable, filterDuplicateKeys);
        } else {
            final IHistoricalEntry<V> fristValueFromCache = cachedIterable.getFirstValueFromCache();
            final List<IHistoricalEntry<V>> remainingResult = delegate.getPreviousEntriesList(query,
                    fristValueFromCache.getKey(), newUnitsBack + 1, filterDuplicateKeys);
            final List<IHistoricalEntry<V>> remainingResultNoDuplicate = remainingResult.subList(0,
                    remainingResult.size() - 1);

            final IHistoricalEntry<V> firstResultEntry = remainingResultNoDuplicate.get(0);
            final Integer maximumSize = getParent().getMaximumSize();
            if ((maximumSize == null || cachedPreviousEntries.size() < maximumSize)
                    && firstResultEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
                //we have some data that can be merged at the start of the result
                final int fromIndex = 0;
                final int toIndex = remainingResultNoDuplicate.size();
                prependCachedEntriesKeepMaximumSize(key, remainingResultNoDuplicate, maximumSize, fromIndex, toIndex);
            }

            final FlatteningIterable<IHistoricalEntry<V>> concat = new FlatteningIterable<IHistoricalEntry<V>>(
                    WrapperCloseableIterable.maybeWrap(remainingResultNoDuplicate), cachedIterable);
            return maybeFilterDuplicateKeys(concat, filterDuplicateKeys);
        }
    }

    private ICloseableIterable<IHistoricalEntry<V>> maybeFilterDuplicateKeys(
            final ICloseableIterable<IHistoricalEntry<V>> result, final boolean filterDuplicateKeys) {
        if (!filterDuplicateKeys) {
            return result;
        } else {
            return new ASkippingIterable<IHistoricalEntry<V>>(result) {

                private FDate prevKey = FDate.MIN_DATE;

                @Override
                protected boolean skip(final IHistoricalEntry<V> element) {
                    final FDate elementKey = element.getKey();
                    final boolean skip = prevKey.equalsNotNullSafe(elementKey);
                    prevKey = elementKey;
                    return skip;
                }
            };
        }
    }

    private class CachedEntriesSubListIterable implements ICloseableIterable<IHistoricalEntry<V>> {

        private final int modCountSnapshot = TrailingHistoricalCacheQueryCore.this.modCount;
        private final int fromIndex;
        private final int toIndex;
        private final int newUnitsBack;
        private final List<IHistoricalEntry<V>> list;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        CachedEntriesSubListIterable(final int fromIndex, final int toIndex, final int newUnitsBack) {
            this.fromIndex = fromIndex - modIncrementIndex;
            this.toIndex = toIndex - modIncrementIndex;
            this.newUnitsBack = newUnitsBack;
            this.list = cachedPreviousEntries;
        }

        public IHistoricalEntry<V> getFirstValueFromCache() {
            return list.get(fromIndex + modIncrementIndex);
        }

        public int getNewUnitsBack() {
            return newUnitsBack;
        }

        @Override
        public ICloseableIterator<IHistoricalEntry<V>> iterator() {
            if (modCountSnapshot != modCount) {
                throw new IllegalStateException("Iterator is too old: modCountSnapshot[" + modCountSnapshot
                        + "] != modCount[" + modCount + "]");
            }
            return WrapperCloseableIterable
                    .maybeWrap(list.subList(fromIndex + modIncrementIndex, toIndex + modIncrementIndex))
                    .iterator();
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private CachedEntriesSubListIterable fillFromCacheAsFarAsPossible(final int unitsBack,
            final FDate skippingKeysAbove) throws ResetCacheException {
        //prefill what is possible and add suffixes by query as needed
        final int cachedToIndex;
        if (skippingKeysAbove != null) {
            cachedToIndex = bisect(skippingKeysAbove, cachedPreviousEntries, unitsBack, this);
        } else {
            cachedToIndex = cachedPreviousEntries.size() - 1;
        }

        final int toIndex = cachedToIndex + 1;
        final int fromIndex = Math.max(0, toIndex - unitsBack);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;

        return new CachedEntriesSubListIterable(fromIndex, toIndex, newUnitsBack);
    }

    @Override
    protected int bisect(final FDate skippingKeysAbove, final List<IHistoricalEntry<V>> list, final Integer unitsBack,
            final ACachedEntriesHistoricalCacheQueryCore<V> useIndex) throws ResetCacheException {
        return delegate.bisect(skippingKeysAbove, list, unitsBack, useIndex);
    }

    private void prependCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing,
            final int trailingCountFoundInCache) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInCache - 1; i >= 0; i--) {
            final IHistoricalEntry<V> prependEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final IHistoricalEntry<V> firstCachedEntry = getFirstCachedEntry();
                if (!prependEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
                    throw new IllegalStateException("prependEntry [" + prependEntry.getKey()
                            + "] should be before firstCachedEntry [" + firstCachedEntry.getKey() + "]");
                }
            }
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(prependEntry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(modCount, -1 - modIncrementIndex));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, prependEntry.getValue());
            cachedPreviousEntries.add(0, indexedEntry);
            modIncrementIndex++;
        }
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null && cachedPreviousEntries.size() > maximumSize) {
            throw new IllegalStateException("maximumSize [" + maximumSize
                    + "] was exceeded during prependCachedEntries: " + cachedPreviousEntries.size());
        }
    }

    private void appendCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing,
            final int trailingCountFoundInQuery) throws ResetCacheException {
        for (int i = trailing.size() - trailingCountFoundInQuery; i < trailing.size(); i++) {
            final IHistoricalEntry<V> appendEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final IHistoricalEntry<V> lastCachedEntry = getLastCachedEntry();
                if (!appendEntry.getKey().isAfterNotNullSafe(lastCachedEntry.getKey())) {
                    throw new ResetCacheException("appendEntry [" + appendEntry.getKey()
                            + "] should be after lastCachedEntry [" + lastCachedEntry.getKey() + "]");
                }
            }
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(appendEntry.getKey());
            indexedKey.putQueryCoreIndex(this,
                    new QueryCoreIndex(modCount, cachedPreviousEntries.size() - modIncrementIndex));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, appendEntry.getValue());
            cachedPreviousEntries.add(indexedEntry);
        }
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this,
                new QueryCoreIndex(modCount, cachedPreviousEntries.size() - 1 - modIncrementIndex));
        Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            maximumSize = maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(0);
                modIncrementIndex--;
            }
        }
    }

    private List<IHistoricalEntry<V>> getPreviousEntries_newerData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final boolean filterDuplicateKeys,
            final IHistoricalEntry<V> firstCachedEntry, final IHistoricalEntry<V> lastCachedEntry)
            throws ResetCacheException {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        mergeResult(key, firstCachedEntry, lastCachedEntry, result);
        return result;
    }

    private void mergeResult(final FDate key, final IHistoricalEntry<V> firstCachedEntry,
            final IHistoricalEntry<V> lastCachedEntry, final List<IHistoricalEntry<V>> result)
            throws ResetCacheException {
        final IHistoricalEntry<V> firstResultEntry = result.get(0);
        final IHistoricalEntry<V> lastResultEntry = result.get(result.size() - 1);
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

        final Integer maximumSize = getParent().getMaximumSize();
        if ((maximumSize == null || cachedPreviousEntries.size() < maximumSize)
                && firstResultEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //we have some data that can be merged at the start of the result
            final int fromIndex = 0;
            final int toIndex = delegate.bisect(firstCachedEntry.getKey(), result, null, null);
            prependCachedEntriesKeepMaximumSize(key, result, maximumSize, fromIndex, toIndex);
        }
    }

    private void prependCachedEntriesKeepMaximumSize(final FDate key, final List<IHistoricalEntry<V>> result,
            final Integer maximumSize, final int fromIndex, final int toIndex) throws ResetCacheException {
        int usedFromIndex = fromIndex;
        if (maximumSize != null) {
            int maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - maximumSize - 1;
            final int increasedMaximumSize = maybeIncreaseMaximumSize(
                    cachedPreviousEntries.size() + maximumSizeExceededBy);
            maximumSizeExceededBy = cachedPreviousEntries.size() + toIndex - increasedMaximumSize;
            if (maximumSizeExceededBy > 0) {
                usedFromIndex = maximumSizeExceededBy;
            }
        }
        final int sizeToPrepend = toIndex - usedFromIndex;
        final int trailingFoundInCache = result.size() - sizeToPrepend;
        prependCachedEntries(key, result, trailingFoundInCache);
    }

    private ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries_tooOldData(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesList(query, key, shiftBackUnits,
                filterDuplicateKeys);
        return WrapperCloseableIterable.maybeWrap(result);
    }

    @Override
    public void clear() {
        if (cachedQueryActiveLock.tryLock()) {
            try {
                if (cachedQueryActive) {
                    return;
                }
                resetForRetry();
                countResets = 0;
            } finally {
                cachedQueryActiveLock.unlock();
            }
        }
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        delegate.increaseMaximumSize(maximumSize);
    }

    @Override
    public void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
        if (!cachedQueryActiveLock.tryLock()) {
            return;
        }
        try {
            if (cachedQueryActive) {
                return;
            }
            delegate.putPrevious(previousKey, value, valueKey);
            try {
                if (!cachedPreviousEntries.isEmpty()) {
                    final IHistoricalEntry<V> lastEntry = getLastCachedEntry();
                    if (lastEntry.getKey().isBeforeNotNullSafe(previousKey)) {
                        //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
                        replaceCachedEntries(valueKey, Arrays.asList(ImmutableHistoricalEntry.of(valueKey, value)));
                        return;
                    } else if (lastEntry.getKey().isAfterNotNullSafe(previousKey)) {
                        return;
                    }
                }
                appendCachedEntry(valueKey, null, ImmutableHistoricalEntry.of(valueKey, value));
            } catch (final ResetCacheException e) {
                //should not happen here
                throw new RuntimeException(e);
            }
        } finally {
            cachedQueryActiveLock.unlock();
        }
    }

    @Override
    public void putPreviousKey(final FDate previousKey, final FDate valueKey) {
        if (!cachedQueryActiveLock.tryLock()) {
            return;
        }
        try {
            if (cachedQueryActive) {
                return;
            }
            if (cachedPreviousEntries.isEmpty()) {
                final IHistoricalEntry<V> newEntry = getParent().computeEntry(valueKey);
                putPrevious(previousKey, newEntry.getValue(), newEntry.getKey());
                return;
            }
            final IHistoricalEntry<V> lastEntry = getLastCachedEntry();
            if (!lastEntry.getKey().equalsNotNullSafe(previousKey)) {
                if (lastEntry.getKey().isBeforeNotNullSafe(previousKey)) {
                    final IHistoricalEntry<V> newEntry = getParent().computeEntry(valueKey);
                    putPrevious(previousKey, newEntry.getValue(), newEntry.getKey());
                }
                return;
            }
            final IHistoricalEntry<V> newEntry = getParent().computeEntry(valueKey);
            getParent().getPutProvider().put(newEntry, lastEntry, true);
        } catch (final ResetCacheException e) {
            //should not happen here
            throw new RuntimeException(e);
        } finally {
            cachedQueryActiveLock.unlock();
        }
    }

}
