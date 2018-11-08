package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.index.QueryCoreIndex;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class CachedHistoricalCacheQueryCore<V> extends ACachedResultHistoricalCacheQueryCore<V> {

    private static final int INITIAL_MAX_CACHED_INDEX = 10000;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(CachedHistoricalCacheQueryCore.class);
    private static final int REQUIRED_SIZE_MULTIPLICATOR = 2;
    private static final int COUNT_RESETS_BEFORE_WARNING = 100;

    private final DefaultHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("cachedQueryActiveLock")
    private int countResets = 0;
    private volatile int maxCachedIndex;
    private final ILock cachedQueryActiveLock = Locks
            .newReentrantLock(getClass().getSimpleName() + "_cachedQueryActiveLock");
    @GuardedBy("cachedQueryActiveLock")
    private boolean cachedQueryActive = false;

    public CachedHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new DefaultHistoricalCacheQueryCore<V>(parent);
        this.maxCachedIndex = Integers.max(INITIAL_MAX_CACHED_INDEX, parent.getMaximumSize());
    }

    @Override
    protected IHistoricalCacheQueryCore<V> getDelegate() {
        return delegate;
    }

    @Override
    public Integer maybeIncreaseMaximumSize(final int requiredSize) {
        final int adjRequiredSize = requiredSize * REQUIRED_SIZE_MULTIPLICATOR;
        final Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null && maximumSize < requiredSize) {
            getParent().increaseMaximumSize(adjRequiredSize, CachedHistoricalCacheQueryCore.class.getSimpleName()
                    + " encountered higher shiftBackUnits of " + requiredSize);
        }
        maxCachedIndex = Math.max(maxCachedIndex, adjRequiredSize);
        return getParent().getMaximumSize();
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        if (shiftBackUnits == 0) {
            return getDelegate().getPreviousEntry(query, key, 0);
        } else {
            //use arraylist since we don't want to have the overhead of filtering duplicates
            final int incrementedShiftBackUnits = shiftBackUnits + 1;
            final List<IHistoricalEntry<V>> previousEntries = getPreviousEntriesList(query, key,
                    incrementedShiftBackUnits);
            if (previousEntries.isEmpty()) {
                return null;
            } else {
                return previousEntries.get(0);
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
            final List<IHistoricalEntry<V>> result = getPreviousEntriesList(query, key, shiftBackUnits);
            return WrapperCloseableIterable.maybeWrap(result);
        }
    }

    public List<IHistoricalEntry<V>> getPreviousEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        final boolean cachedQueryActiveLocked = cachedQueryActiveLock.tryLock();
        if (!cachedQueryActiveLocked || cachedQueryActive) {
            try {
                //prevent nested/recursive cached queries that might f**k up the cache
                final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
                final List<IHistoricalEntry<V>> result = defaultGetPreviousEntries(query, key, shiftBackUnits,
                        trailing);
                return result;
            } finally {
                if (cachedQueryActiveLocked) {
                    cachedQueryActiveLock.unlock();
                }
            }
        } else {
            try {
                cachedQueryActive = true;
                try {
                    final List<IHistoricalEntry<V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                            shiftBackUnits);
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
                        final List<IHistoricalEntry<V>> result = tryCachedGetPreviousEntriesIfAvailable(query, key,
                                shiftBackUnits);
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

    private List<IHistoricalEntry<V>> tryCachedGetPreviousEntriesIfAvailable(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits)
            throws ResetCacheException {
        final List<IHistoricalEntry<V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, key, shiftBackUnits);
        } else {
            final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
            result = defaultGetPreviousEntries(query, key, shiftBackUnits, trailing);
            updateCachedPreviousResult(query, shiftBackUnits, result);
        }
        return result;
    }

    private List<IHistoricalEntry<V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) throws ResetCacheException {
        final IHistoricalEntry<V> firstCachedEntry = getFirstCachedEntry();
        final IHistoricalEntry<V> lastCachedEntry = getLastCachedEntry();
        if (key.equalsNotNullSafe(cachedPreviousEntriesKey) || key.equalsNotNullSafe(lastCachedEntry.getKey())) {
            final List<IHistoricalEntry<V>> tryCachedPreviousResult = tryCachedPreviousResult_sameKey(query,
                    shiftBackUnits);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
                final List<IHistoricalEntry<V>> result = cachedGetPreviousEntries_sameKey(query, shiftBackUnits, key,
                        trailing);
                updateCachedPreviousResult(query, shiftBackUnits, result);
                return result;
            }
        } else if ((key.isAfterNotNullSafe(cachedPreviousEntriesKey)
                || key.isAfterNotNullSafe(lastCachedEntry.getKey()))
                /*
                 * when we go a higher key and only want to load 1 value, we can just go with direct map access since we
                 * won't hit cache anyway because first access will be against map anyway. we make sure to not replace
                 * the cached values in this case in the default query
                 */
                && shiftBackUnits > 1) {
            final List<IHistoricalEntry<V>> tryCachedPreviousResult = tryCachedPreviousResult_incrementedKey(query,
                    shiftBackUnits, key);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
                final List<IHistoricalEntry<V>> result = cachedGetPreviousEntries_incrementedKey(query, shiftBackUnits,
                        key, trailing);
                updateCachedPreviousResult(query, shiftBackUnits, result);
                return result;
            }
        } else if (key.isBeforeOrEqualToNotNullSafe(cachedPreviousEntriesKey)
                && key.isAfterOrEqualToNotNullSafe(firstCachedEntry.getKey())
                && key.isBeforeOrEqualToNotNullSafe(lastCachedEntry.getKey())) {
            final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
            return cachedGetPreviousEntries_decrementedKey(query, shiftBackUnits, key, trailing);
        } else {
            /*
             * value will not be found in cache (we are before the first cached entry), so we just go with the default
             * query and renew the cache if possible; jumping around wildly in the history is expensive right now
             */
            final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
            final List<IHistoricalEntry<V>> result = defaultGetPreviousEntries(query, key, shiftBackUnits, trailing);
            updateCachedPreviousResult(query, shiftBackUnits, result);
            return result;
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     * 
     * @throws ResetCacheException
     */

    private List<IHistoricalEntry<V>> tryCachedPreviousResult_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key)
            throws ResetCacheException {
        if (cachedPreviousEntries.isEmpty() || cachedPreviousResult_shiftBackUnits == null) {
            return null;
        }
        if (shiftBackUnits > cachedPreviousResult_shiftBackUnits + 1) {
            resetCachedPreviousResult();
            return null;
        }
        final FDate lastCachedEntryKey = determineConsistentLastCachedEntryKey();

        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        IHistoricalEntry<V> latestValue = null;
        while (!impl.iterationFinished()) {
            final IHistoricalEntry<V> value = impl.getResult();
            if (value == null) {
                resetCachedPreviousResult();
                return null; //abort since we could not find any values
            } else {
                if (value.getKey().equalsNotNullSafe(lastCachedEntryKey)) {
                    break; //continue with tryCachedPreviousResult_sameKey
                } else {
                    if (latestValue == null) {
                        latestValue = value;
                    } else {
                        resetCachedPreviousResult();
                        return null; //abort since we could not find just one new value
                    }
                }
            }
        }

        appendCachedEntryAndResult(key, shiftBackUnits, latestValue);
        return tryCachedPreviousResult_sameKey(query, shiftBackUnits);
    }

    private List<IHistoricalEntry<V>> cachedGetPreviousEntries_decrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<IHistoricalEntry<V>> trailing) throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, key);
        if (unitsBack == -1) {
            //we could satisfy the query completely with cached values
            //cached values don't have to be updated
            return trailing;
        } else {
            final int trailingCountFoundInCache = trailing.size();
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            prependCachedEntries(key, trailing, trailingCountFoundInCache);
            return trailing;
        }
    }

    private List<IHistoricalEntry<V>> cachedGetPreviousEntries_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<IHistoricalEntry<V>> trailing) throws ResetCacheException {
        int unitsBack = fillFromQueryUntilCacheCanBeUsed(query, shiftBackUnits, key, trailing);

        if (unitsBack == -1) {
            //we can replace the cache since it was too far away anyway
            //explicitly not calling updateCachedPreviousResult for it to be reset
            replaceCachedEntries(key, trailing);
            //we were above the cache and did not find anything useful in the cache, so we filled everything from query
            return trailing;
        }

        final int trailingCountFoundInQuery = trailing.size();

        //then fill the rest from the cache as far as possible
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);

        if (unitsBack == -1) {
            //add elments from query since we found newer values and were able to use the cache
            appendCachedEntries(key, trailing, trailingCountFoundInQuery);

            //we could satisfy the query completely with cached values
            return trailing;
        } else {
            //and use the query again if there are missing elements at the end
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            //we can replace the cache since we extended it in both directions
            //explicitly not calling updateCachedPreviousResult for it to be reset
            replaceCachedEntries(key, trailing);
            return trailing;
        }
    }

    private int fillFromQueryUntilCacheCanBeUsed(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<IHistoricalEntry<V>> trailing)
            throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        final List<IHistoricalEntry<V>> trailingReverse;
        if (trailing.isEmpty()) {
            trailingReverse = trailing;
        } else {
            trailingReverse = newEntriesList(shiftBackUnits);
        }
        while (unitsBack >= 0 && !impl.iterationFinished()) {
            final IHistoricalEntry<V> value = impl.getResult();
            if (value != null) {
                if (value.getKey().equalsNotNullSafe(lastCachedEntryKey)) {
                    break; //continue with fillFromCacheAsFarAsPossible
                } else {
                    if (trailingReverse.add(value)) {
                        unitsBack--;
                    } else {
                        unitsBack = -1; //break
                    }
                }
            } else {
                unitsBack = -1; //break
            }
        }
        Collections.reverse(trailingReverse);
        if (trailing != trailingReverse) {
            trailing.addAll(0, trailingReverse);
        }
        return unitsBack;
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
        Integer maximumSize = getParent().getMaximumSize();
        if (maximumSize != null) {
            maximumSize = maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                /*
                 * since we are going further back in time, we have to remove current values. We expect to go further
                 * back and will live with the cost of loading again the current values next time
                 */
                cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
            }
            //reset cached results and set new marker so that we don't go accidentally into sameKey algorithm
            resetCachedPreviousResult();
            cachedPreviousEntriesKey = (IndexedFDate) getLastCachedEntry().getKey();
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
        cachedPreviousEntriesKey = indexedKey;
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

    private List<IHistoricalEntry<V>> cachedGetPreviousEntries_sameKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<IHistoricalEntry<V>> trailing) throws ResetCacheException {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);
        if (unitsBack == -1) {
            //maybe cached previous result has smaller shift back
            resetCachedPreviousResult();
            //we could satisfy the query completely with cached values
            //cached values don't have to be updated
            return trailing;
        } else {
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            //explicitly not calling updateCachedPreviousResult for it to be reset
            replaceCachedEntries(key, trailing);
            return trailing;
        }
    }

    private void loadFurtherTrailingValuesViaQuery(final IHistoricalCacheQueryInternalMethods<V> query,
            final List<IHistoricalEntry<V>> trailing, final int unitsBack) throws ResetCacheException {
        assertUnitsBackNotExchausted(unitsBack);
        final FDate lastTrailingKey = trailing.get(0).getKey();
        //we need to load further values from the map
        final int skipFirstValueIncrement = 1;
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, lastTrailingKey,
                unitsBack + skipFirstValueIncrement);
        impl.setIterations(skipFirstValueIncrement);
        int newUnitsBack = unitsBack;
        newUnitsBack = fillFromQuery(query, trailing, impl, newUnitsBack);
    }

    private int fillFromQuery(final IHistoricalCacheQueryInternalMethods<V> query,
            final List<IHistoricalEntry<V>> trailing, final GetPreviousEntryQueryImpl<V> impl, final int unitsBack) {
        int newUnitsBack = unitsBack;
        final List<IHistoricalEntry<V>> trailingReverse;
        if (trailing.isEmpty()) {
            trailingReverse = trailing;
        } else {
            trailingReverse = newEntriesList(unitsBack);
        }
        while (newUnitsBack >= 0 && !impl.iterationFinished()) {
            final IHistoricalEntry<V> value = impl.getResult();
            if (value != null) {
                if (!trailingReverse.add(value)) {
                    newUnitsBack = -1; //break
                    break;
                }
            } else {
                break;
            }
            newUnitsBack--;
        }
        Collections.reverse(trailingReverse);
        if (trailing != trailingReverse) {
            trailing.addAll(0, trailingReverse);
        }
        return newUnitsBack;
    }

    private void assertUnitsBackNotExchausted(final int unitsBack) throws ResetCacheException {
        if (unitsBack < 0) {
            throw new IllegalStateException("unitsBack should not become smaller than -1: " + unitsBack);
        }
    }

    private List<IHistoricalEntry<V>> defaultGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits, final List<IHistoricalEntry<V>> trailing) {
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromQuery(query, trailing, impl, unitsBack);
        //explicitly not calling updateCachedPreviousResult for it to be reset
        replaceCachedEntries(key, trailing);
        return trailing;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int fillFromCacheAsFarAsPossible(final List<IHistoricalEntry<V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) throws ResetCacheException {
        //prefill what is possible and add suffixes by query as needed
        final int cachedToIndex;
        if (skippingKeysAbove != null) {
            cachedToIndex = bisect(skippingKeysAbove, cachedPreviousEntries, unitsBack, this);
        } else {
            cachedToIndex = cachedPreviousEntries.size() - 1;
        }

        final int toIndex = cachedToIndex + 1;
        final int fromIndex = Math.max(0, toIndex - unitsBack - 1);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;
        trailing.addAll(0, cachedPreviousEntries.subList(fromIndex, toIndex));

        return newUnitsBack;
    }

    //CHECKSTYLE:OFF
    @Override
    public int bisect(final FDate skippingKeysAbove, final List<IHistoricalEntry<V>> list, final Integer unitsBack,
            final ACachedEntriesHistoricalCacheQueryCore<V> useIndex) throws ResetCacheException {
        //CHECKSTYLE:ON
        int lo = 0;
        int hi = list.size();
        if (unitsBack != null) {
            final FDate loTime = list.get(lo).getKey();
            if (skippingKeysAbove.isBeforeOrEqualToNotNullSafe(loTime) && hi >= maxCachedIndex) {
                throw new ResetCacheException("Not enough data in cache for fillFromCacheAsFarAsPossible [" + unitsBack
                        + "/" + maxCachedIndex + "/" + (hi - 1) + "]");
            }
        }

        if (useIndex != null) {
            final IndexedFDate indexedSkippingKeysAbove = IndexedFDate.maybeUnwrap(skippingKeysAbove);
            if (indexedSkippingKeysAbove != null) {
                final QueryCoreIndex queryCoreIndex = indexedSkippingKeysAbove.getQueryCoreIndex(useIndex);
                if (queryCoreIndex != null && queryCoreIndex.getModCount() == useIndex.modCount) {
                    final int index = queryCoreIndex.getIndex() + useIndex.modIncrementIndex;
                    if (index >= 0) {
                        return index;
                    }
                }
            }
        }

        //bisect
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            final FDate midKey = list.get(mid).getKey();
            final int compareTo = midKey.compareToNotNullSafe(skippingKeysAbove);
            switch (compareTo) {
            case -1:
                lo = mid + 1;
                break;
            case 0:
                registerIndex(skippingKeysAbove, mid, useIndex);
                return mid;
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        final FDate loTime = list.get(lo).getKey();
        if (loTime.isAfterNotNullSafe(skippingKeysAbove)) {
            final int index = lo - 1;
            registerIndex(skippingKeysAbove, index, useIndex);
            return index;
        } else {
            registerIndex(skippingKeysAbove, lo, useIndex);
            return lo;
        }
    }

    private void registerIndex(final FDate key, final int index,
            final ACachedEntriesHistoricalCacheQueryCore<V> useIndex) {
        if (useIndex != null) {
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
            indexedKey.putQueryCoreIndex(useIndex,
                    new QueryCoreIndex(useIndex.modCount, index - useIndex.modIncrementIndex));
        }
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
        this.maxCachedIndex = Math.max(maxCachedIndex, maximumSize);
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
            if (cachedPreviousEntries.isEmpty()) {
                return;
            }
            final IHistoricalEntry<V> lastEntry = getLastCachedEntry();
            if (lastEntry == null) {
                return;
            }
            if (!lastEntry.getKey().equalsNotNullSafe(previousKey)) {
                return;
            }
            appendCachedEntryAndResult(valueKey, null, ImmutableHistoricalEntry.of(valueKey, value));
        } catch (final ResetCacheException e) {
            //should not happen here
            throw new RuntimeException(e);
        } finally {
            cachedQueryActiveLock.unlock();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
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
                return;
            }
            final IHistoricalEntry<V> lastEntry = getLastCachedEntry();
            if (lastEntry == null) {
                return;
            }
            if (!lastEntry.getKey().equalsNotNullSafe(previousKey)) {
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
