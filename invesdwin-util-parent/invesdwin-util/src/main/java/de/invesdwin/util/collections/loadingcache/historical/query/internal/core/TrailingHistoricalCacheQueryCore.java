package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.AFastToListCloseableIterator;
import de.invesdwin.util.collections.iterable.collection.fast.AFastToListSkippingIterable;
import de.invesdwin.util.collections.iterable.collection.fast.FastToListFlatteningIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class TrailingHistoricalCacheQueryCore<V> extends ACachedEntriesHistoricalCacheQueryCore<V> {

    private final CachedHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("cachedQueryActiveLock")
    private int countResets = 0;
    private final ILock cachedQueryActiveLock;
    @GuardedBy("cachedQueryActiveLock")
    private final MutableBoolean cachedQueryActive = new MutableBoolean(false);

    public TrailingHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        //CHECKSTYLE:OFF no cycle detection needed because we always back off locks via tryLock
        this.cachedQueryActiveLock = Locks.maybeWrap(
                TrailingHistoricalCacheQueryCore.class.getSimpleName() + "_cachedQueryActiveLock", new ReentrantLock());
        //CHECKSTYLE:ON
        //reuse lock so that set methods on sublist are synchronized
        this.delegate = new CachedHistoricalCacheQueryCore<V>(parent, cachedQueryActiveLock, cachedQueryActive);
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
            final int incrementedShiftBackUnits = shiftBackUnits + 1;
            final IFastToListCloseableIterable<IHistoricalEntry<V>> iterable = getPreviousEntriesList(query, key,
                    incrementedShiftBackUnits);
            final IHistoricalEntry<V> tail = iterable.getTail();
            try (IFastToListCloseableIterator<IHistoricalEntry<V>> previousEntries = iterable.iterator()) {
                final IHistoricalEntry<V> next = previousEntries.next();
                try {
                    if (tail != null && tail.getKey().isBefore(key)) {
                        return previousEntries.next();
                    }
                } catch (final NoSuchElementException e) {
                    //end reached
                }
                return next;
            } catch (final NoSuchElementException e) {
                return null;
            }
        }
    }

    @Override
    public IFastToListCloseableIterable<IHistoricalEntry<V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final IHistoricalEntry<V> entry = getDelegate().getPreviousEntry(query, key, 0);
            return new SingleValueIterable<IHistoricalEntry<V>>(entry);
        } else {
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result = getPreviousEntriesList(query, key,
                    shiftBackUnits);
            return result;
        }
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> getPreviousEntriesList(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        final boolean cachedQueryActiveLocked = cachedQueryActiveLock.tryLock();
        /*
         * cachedQueryActive is only checked here for recursive queries where the lock is anyway already held but we
         * don't want to do another nested cache lookup. Because of that, cachedQueryActive does not need to be
         * volatile!
         */
        if (!cachedQueryActiveLocked || cachedQueryActive.booleanValue()) {
            try {
                return delegate.getPreviousEntries(query, key, shiftBackUnits);
            } finally {
                if (cachedQueryActiveLocked) {
                    cachedQueryActiveLock.unlock();
                }
            }
        } else {
            cachedQueryActive.setTrue();
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result = tryCachedGetPreviousEntriesIfAvailable(
                    query, key, shiftBackUnits);
            return new UnlockingResultIterable(result);
        }
    }

    @Override
    protected Integer maybeIncreaseMaximumSize(final int requiredSize) {
        return delegate.maybeIncreaseMaximumSize(requiredSize);
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        final IFastToListCloseableIterable<IHistoricalEntry<V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, key, shiftBackUnits);
        } else {
            result = defaultGetPreviousEntries(query, key, shiftBackUnits);
        }
        return result;
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> defaultGetPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesListUnlocked(query, key, shiftBackUnits);
        //explicitly not calling updateCachedPreviousResult for it to be reset, it will get updated later
        replaceCachedEntries(key, result);
        return (IFastToListCloseableIterable<IHistoricalEntry<V>>) WrapperCloseableIterable.maybeWrap(result);
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> cachedGetPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        final IHistoricalEntry<V> firstCachedEntry = getFirstCachedEntry();
        final IHistoricalEntry<V> lastCachedEntry = getLastCachedEntry();
        if (key.equalsNotNullSafe(lastCachedEntry.getKey())) {
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(
                    query, key, shiftBackUnits, firstCachedEntry, lastCachedEntry);
            return result;
        } else if (key.isBeforeNotNullSafe(firstCachedEntry.getKey())) {
            //a request that is way too old
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result = getPreviousEntries_tooOldData(query, key,
                    shiftBackUnits);
            return result;
        } else if (key.isAfterNotNullSafe(lastCachedEntry.getKey())) {
            //a request that might lead to newer data that can be appended
            final List<IHistoricalEntry<V>> result = getPreviousEntries_newerData(query, key, shiftBackUnits,
                    firstCachedEntry, lastCachedEntry);
            return (IFastToListCloseableIterable<IHistoricalEntry<V>>) WrapperCloseableIterable.maybeWrap(result);
        } else {
            //somewhere in the middle
            //check units back and either fulfill directly
            //or fill as far as possible, do another request for remaining and then prepend more data as much as size allows
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result = cachedGetPreviousEntries_somewhereInTheMiddle(
                    query, key, shiftBackUnits, firstCachedEntry, lastCachedEntry);
            //like decremented key, not updating cached previous result
            return result;
        }
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> cachedGetPreviousEntries_somewhereInTheMiddle(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final IHistoricalEntry<V> firstCachedEntry, final IHistoricalEntry<V> lastCachedEntry) {
        final CachedEntriesSubListIterable<V> cachedIterable = fillFromCacheAsFarAsPossible(shiftBackUnits, key);
        final int newUnitsBack = cachedIterable.getNewUnitsBack();
        if (newUnitsBack <= 0) {
            return filterDuplicateKeys(cachedIterable);
        } else {
            final IHistoricalEntry<V> fristValueFromCache = cachedIterable.getFirstValueFromCache();
            final List<IHistoricalEntry<V>> remainingResult = delegate.getPreviousEntriesListUnlocked(query,
                    fristValueFromCache.getKey(), newUnitsBack + 1);
            final List<IHistoricalEntry<V>> remainingResultNoDuplicate;
            if (remainingResult.size() == 1) {
                remainingResultNoDuplicate = remainingResult;
            } else {
                remainingResultNoDuplicate = remainingResult.subList(0, remainingResult.size() - 1);
            }

            final IHistoricalEntry<V> firstResultEntry = remainingResultNoDuplicate.get(0);
            final Integer maximumSize = getParent().getMaximumSize();
            if ((maximumSize == null || cachedPreviousEntries.size() < maximumSize)
                    && firstResultEntry.getKey().isBeforeNotNullSafe(firstCachedEntry.getKey())) {
                //we have some data that can be merged at the start of the result
                final int fromIndex = 0;
                final int toIndex = remainingResultNoDuplicate.size();
                prependCachedEntriesKeepMaximumSize(key, remainingResultNoDuplicate, maximumSize, fromIndex, toIndex);
            }

            final FastToListFlatteningIterable<IHistoricalEntry<V>> concat = new FastToListFlatteningIterable<IHistoricalEntry<V>>(
                    WrapperCloseableIterable.maybeWrap(remainingResultNoDuplicate), cachedIterable);
            return filterDuplicateKeys(concat);
        }
    }

    private IFastToListCloseableIterable<IHistoricalEntry<V>> filterDuplicateKeys(
            final IFastToListCloseableIterable<IHistoricalEntry<V>> result) {
        return new AFastToListSkippingIterable<IHistoricalEntry<V>>(result) {

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

    private CachedEntriesSubListIterable<V> fillFromCacheAsFarAsPossible(final int unitsBack,
            final FDate skippingKeysAbove) {
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

        return new CachedEntriesSubListIterable<V>(cachedPreviousEntries, cachedPreviousEntries_modIncrementIndex,
                fromIndex, toIndex, newUnitsBack);
    }

    @Override
    protected int bisect(final FDate skippingKeysAbove, final List<IHistoricalEntry<V>> list, final Integer unitsBack,
            final ACachedEntriesHistoricalCacheQueryCore<V> useIndex) {
        return delegate.bisect(skippingKeysAbove, list, unitsBack, useIndex);
    }

    private void prependCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing,
            final int trailingCountFoundInCache) {
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
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(cachedPreviousEntries_modCount,
                    -1 - cachedPreviousEntries_modIncrementIndex.intValue()));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, prependEntry.getValue());
            cachedPreviousEntries.add(0, indexedEntry);
            cachedPreviousEntries_modIncrementIndex.increment();
        }
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null && cachedPreviousEntries.size() > maximumSize) {
            throw new IllegalStateException("maximumSize [" + maximumSize
                    + "] was exceeded during prependCachedEntries: " + cachedPreviousEntries.size());
        }
    }

    private void appendCachedEntries(final FDate key, final List<IHistoricalEntry<V>> trailing,
            final int trailingCountFoundInQuery) {
        for (int i = trailing.size() - trailingCountFoundInQuery; i < trailing.size(); i++) {
            final IHistoricalEntry<V> appendEntry = trailing.get(i);
            if (!cachedPreviousEntries.isEmpty()) {
                final IHistoricalEntry<V> lastCachedEntry = getLastCachedEntry();
                if (!appendEntry.getKey().isAfterNotNullSafe(lastCachedEntry.getKey())) {
                    throw new IllegalStateException("appendEntry [" + appendEntry.getKey()
                            + "] should be after lastCachedEntry [" + lastCachedEntry.getKey() + "]");
                }
            }
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(appendEntry.getKey());
            indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(cachedPreviousEntries_modCount,
                    cachedPreviousEntries.size() - cachedPreviousEntries_modIncrementIndex.intValue()));
            final IHistoricalEntry<V> indexedEntry = ImmutableHistoricalEntry.of(indexedKey, appendEntry.getValue());
            cachedPreviousEntries.add(indexedEntry);
        }
        final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
        indexedKey.putQueryCoreIndex(this, new QueryCoreIndex(cachedPreviousEntries_modCount,
                cachedPreviousEntries.size() - 1 - cachedPreviousEntries_modIncrementIndex.intValue()));
        Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            maximumSize = maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(0);
                cachedPreviousEntries_modIncrementIndex.decrement();
            }
        }
    }

    private List<IHistoricalEntry<V>> getPreviousEntries_newerData(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final IHistoricalEntry<V> firstCachedEntry,
            final IHistoricalEntry<V> lastCachedEntry) {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesListUnlocked(query, key, shiftBackUnits);
        mergeResult(key, firstCachedEntry, lastCachedEntry, result);
        return result;
    }

    private void mergeResult(final FDate key, final IHistoricalEntry<V> firstCachedEntry,
            final IHistoricalEntry<V> lastCachedEntry, final List<IHistoricalEntry<V>> result) {
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
            final Integer maximumSize, final int fromIndex, final int toIndex) {
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

    private IFastToListCloseableIterable<IHistoricalEntry<V>> getPreviousEntries_tooOldData(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        final List<IHistoricalEntry<V>> result = delegate.getPreviousEntriesListUnlocked(query, key, shiftBackUnits);
        return (IFastToListCloseableIterable<IHistoricalEntry<V>>) WrapperCloseableIterable.maybeWrap(result);
    }

    @Override
    public void clear() {
        if (cachedQueryActiveLock.tryLock()) {
            try {
                if (cachedQueryActive.booleanValue()) {
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
            if (cachedQueryActive.booleanValue()) {
                return;
            }
            delegate.putPrevious(previousKey, value, valueKey);
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
            if (cachedQueryActive.booleanValue()) {
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
        } finally {
            cachedQueryActiveLock.unlock();
        }
    }

    private static class CachedEntriesSubListIterable<_V>
            implements IFastToListCloseableIterable<IHistoricalEntry<_V>> {

        private final int fromIndex;
        private final int toIndex;
        private final int newUnitsBack;
        private final List<IHistoricalEntry<_V>> list;
        private final MutableInt modIncrementIndex;

        CachedEntriesSubListIterable(final List<IHistoricalEntry<_V>> list, final MutableInt modIncrementIndex,
                final int fromIndex, final int toIndex, final int newUnitsBack) {
            this.list = list;
            this.modIncrementIndex = modIncrementIndex;
            final int modIncrementIndexSnapshot = modIncrementIndex.intValue();
            this.fromIndex = fromIndex - modIncrementIndexSnapshot;
            this.toIndex = toIndex - modIncrementIndexSnapshot;
            this.newUnitsBack = newUnitsBack;
        }

        public IHistoricalEntry<_V> getFirstValueFromCache() {
            return list.get(fromIndex + modIncrementIndex.intValue());
        }

        public int getNewUnitsBack() {
            return newUnitsBack;
        }

        @Override
        public IFastToListCloseableIterator<IHistoricalEntry<_V>> iterator() {
            final List<IHistoricalEntry<_V>> subList = toList();
            return (IFastToListCloseableIterator<IHistoricalEntry<_V>>) WrapperCloseableIterable.maybeWrap(subList)
                    .iterator();
        }

        @Override
        public List<IHistoricalEntry<_V>> toList() {
            final int modIncrementIndexSnapshot = modIncrementIndex.intValue();
            final List<IHistoricalEntry<_V>> subList = list.subList(fromIndex + modIncrementIndexSnapshot,
                    toIndex + modIncrementIndexSnapshot);
            return subList;
        }

        @Override
        public List<IHistoricalEntry<_V>> toList(final List<IHistoricalEntry<_V>> list) {
            list.addAll(toList());
            return list;
        }

        @Override
        public IHistoricalEntry<_V> getHead() {
            final int modIncrementIndexSnapshot = modIncrementIndex.intValue();
            return list.get(fromIndex + modIncrementIndexSnapshot);
        }

        @Override
        public IHistoricalEntry<_V> getTail() {
            final int modIncrementIndexSnapshot = modIncrementIndex.intValue();
            return list.get(toIndex + modIncrementIndexSnapshot - 1);
        }

    }

    private final class UnlockingResultIterable implements IFastToListCloseableIterable<IHistoricalEntry<V>> {
        private final IFastToListCloseableIterable<IHistoricalEntry<V>> result;

        private UnlockingResultIterable(final IFastToListCloseableIterable<IHistoricalEntry<V>> result) {
            this.result = result;
        }

        @Override
        public IFastToListCloseableIterator<IHistoricalEntry<V>> iterator() {
            if (Throwables.isDebugStackTraceEnabled()) {
                return new AFastToListCloseableIterator<IHistoricalEntry<V>>(new TextDescription("%s: %s.%s",
                        getParent(), TrailingHistoricalCacheQueryCore.class.getSimpleName(),
                        UnlockingResultIterable.class.getSimpleName())) {

                    private final UnlockingResultFinalizer<IHistoricalEntry<V>> finalizer = new UnlockingResultFinalizer<IHistoricalEntry<V>>(
                            result.iterator(), cachedQueryActive, cachedQueryActiveLock);

                    {
                        this.finalizer.register(this);
                    }

                    @Override
                    protected boolean innerHasNext() {
                        return finalizer.iterator.hasNext();
                    }

                    @Override
                    protected IHistoricalEntry<V> innerNext() {
                        return finalizer.iterator.next();
                    }

                    @Override
                    protected void innerClose() {
                        finalizer.close();
                    }

                    @Override
                    public List<IHistoricalEntry<V>> toList() {
                        return finalizer.iterator.toList();
                    }

                    @Override
                    public List<IHistoricalEntry<V>> toList(final List<IHistoricalEntry<V>> list) {
                        return finalizer.iterator.toList(list);
                    }

                    @Override
                    public IHistoricalEntry<V> getHead() {
                        return finalizer.iterator.getHead();
                    }

                    @Override
                    public IHistoricalEntry<V> getTail() {
                        return finalizer.iterator.getTail();
                    }

                };
            } else {
                return new IFastToListCloseableIterator<IHistoricalEntry<V>>() {

                    private final UnlockingResultFinalizer<IHistoricalEntry<V>> finalizer = new UnlockingResultFinalizer<IHistoricalEntry<V>>(
                            result.iterator(), cachedQueryActive, cachedQueryActiveLock);

                    {
                        this.finalizer.register(this);
                    }

                    @Override
                    public boolean hasNext() {
                        return finalizer.iterator.hasNext();
                    }

                    @Override
                    public IHistoricalEntry<V> next() {
                        return finalizer.iterator.next();
                    }

                    @Override
                    public void close() {
                        finalizer.close();
                    }

                    @Override
                    public List<IHistoricalEntry<V>> toList() {
                        return finalizer.iterator.toList();
                    }

                    @Override
                    public List<IHistoricalEntry<V>> toList(final List<IHistoricalEntry<V>> list) {
                        return finalizer.iterator.toList(list);
                    }

                    @Override
                    public IHistoricalEntry<V> getHead() {
                        return finalizer.iterator.getHead();
                    }

                    @Override
                    public IHistoricalEntry<V> getTail() {
                        return finalizer.iterator.getTail();
                    }

                };
            }
        }

        @Override
        public List<IHistoricalEntry<V>> toList() {
            return result.toList();
        }

        @Override
        public List<IHistoricalEntry<V>> toList(final List<IHistoricalEntry<V>> list) {
            return result.toList(list);
        }

        @Override
        public IHistoricalEntry<V> getHead() {
            return result.getHead();
        }

        @Override
        public IHistoricalEntry<V> getTail() {
            return result.getTail();
        }
    }

    private static final class UnlockingResultFinalizer<_V> extends AFinalizer {

        private IFastToListCloseableIterator<_V> iterator;
        private final MutableBoolean cachedQueryActive;
        private final ILock cachedQueryActiveLock;

        private UnlockingResultFinalizer(final IFastToListCloseableIterator<_V> iterator,
                final MutableBoolean cachedQueryActive, final ILock cachedQueryActiveLock) {
            this.iterator = iterator;
            this.cachedQueryActive = cachedQueryActive;
            this.cachedQueryActiveLock = cachedQueryActiveLock;
        }

        @Override
        protected void clean() {
            iterator.close();
            iterator = EmptyCloseableIterator.getInstance();
            cachedQueryActive.setFalse();
            cachedQueryActiveLock.unlock();
        }

        @Override
        protected boolean isCleaned() {
            return iterator instanceof EmptyCloseableIterator;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

}
