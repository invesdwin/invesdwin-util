package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.SingleValueIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class CachedHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    private static final int REQUIRED_SIZE_MULTIPLICATOR = 2;
    private final DefaultHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("this")
    private Integer maximumSize;
    @GuardedBy("this")
    private FDate minKeyInDB = null;
    @GuardedBy("this")
    private final List<Entry<FDate, V>> cachedPreviousEntries = new ArrayList<Entry<FDate, V>>();
    @GuardedBy("this")
    private FDate cachedPreviousEntriesKey = null;

    public CachedHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new DefaultHistoricalCacheQueryCore<V>(parent);
        this.maximumSize = parent.getMaximumSize();
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
            final List<Entry<FDate, V>> previousEntries = getPreviousEntriesList(query, key, shiftBackUnits + 1);
            if (previousEntries.isEmpty()) {
                return null;
            } else {
                return previousEntries.get(0);
            }
        }
    }

    private void maybeIncreaseMaximumSize(final int requiredSize) {
        if (maximumSize != null && maximumSize < requiredSize) {
            getParent().increaseMaximumSize(requiredSize * REQUIRED_SIZE_MULTIPLICATOR);
        }
    }

    @Override
    public synchronized ICloseableIterable<Entry<FDate, V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        if (shiftBackUnits == 1) {
            final Entry<FDate, V> entry = delegate.getPreviousEntry(query, key, 0);
            return new SingleValueIterable<Entry<FDate, V>>(entry);
        } else {
            final List<Entry<FDate, V>> result = getPreviousEntriesList(query, key, shiftBackUnits);
            return WrapperCloseableIterable.maybeWrap(result);
        }
    }

    private List<Entry<FDate, V>> getPreviousEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        final FDate adjKey = getParent().adjustKey(key);
        final List<Entry<FDate, V>> result;
        if (!cachedPreviousEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, shiftBackUnits, adjKey);
        } else {
            result = defaultGetPreviousEntries(query, shiftBackUnits, adjKey);
        }
        return result;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey) {
        if (Objects.equals(adjKey, cachedPreviousEntriesKey) || Objects.equals(adjKey, getLastCachedEntry().getKey())) {
            return cachedGetPreviousEntries_sameKey(query, shiftBackUnits, adjKey);
        } else if ((adjKey.isAfter(cachedPreviousEntriesKey) || adjKey.isAfter(getLastCachedEntry().getKey()))
                /*
                 * when we go a higher key and only want to load 1 value, we can just go with direct map access since we
                 * won't hit cache anyway because first access will be against map anyway. we make sure to not replace
                 * the cached values in this case in the default query
                 */
                && shiftBackUnits > 1) {
            return cachedGetPreviousEntries_incrementedKey(query, shiftBackUnits, adjKey);
        } else if (adjKey.isBeforeOrEqual(cachedPreviousEntriesKey)
                && adjKey.isAfterOrEqual(getFirstCachedEntry().getKey())
                && adjKey.isBeforeOrEqual(getLastCachedEntry().getKey())) {
            return cachedGetPreviousEntries_decrementedKey(query, shiftBackUnits, adjKey);
        } else {
            /*
             * value will not be found in cache (we are before the first cached entry), so we just go with the default
             * query and renew the cache if possible; jumping around wildly in the history is expensive right now
             */
            return defaultGetPreviousEntries(query, shiftBackUnits, adjKey);
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_decrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, adjKey);
        if (unitsBack == -1) {
            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            //cached values don't have to be updated
            return trailing;
        } else {
            final int trailingCountFoundInCache = trailing.size();
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            Collections.reverse(trailing);
            prependCachedEntries(adjKey, trailing, trailingCountFoundInCache);
            return trailing;
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        int unitsBack = fillFromQueryUntilCacheCanBeUsed(query, shiftBackUnits, adjKey, trailing);

        if (unitsBack == -1) {
            //we were above the cache and did not find anything useful in the cache, so we filled everything from query
            Collections.reverse(trailing);
            //we can replace the cache since it was too far away anyway
            replaceCachedEntries(adjKey, trailing);
            return trailing;
        }

        final int trailingCountFoundInQuery = trailing.size();

        //then fill the rest from the cache as far as possible
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);

        if (unitsBack == -1) {
            //add elments from query since we found newer values and were able to use the cache
            appendCachedEntries(adjKey, trailing, trailingCountFoundInQuery);

            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            return trailing;
        } else {
            //and use the query again if there are missing elements at the end
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            Collections.reverse(trailing);
            //we can replace the cache since we extended it in both directions
            replaceCachedEntries(adjKey, trailing);
            return trailing;
        }
    }

    private int fillFromQueryUntilCacheCanBeUsed(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey, final List<Entry<FDate, V>> trailing) {
        int unitsBack = shiftBackUnits - 1;
        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, adjKey, shiftBackUnits);
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        while (unitsBack >= 0 && !impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (value.getKey().equals(lastCachedEntryKey)) {
                    break; //continue with fillFromCacheAsFarAsPossible
                } else {
                    trailing.add(value);
                    unitsBack--;
                }
            } else {
                unitsBack = -1; //break
            }
        }
        return unitsBack;
    }

    private void prependCachedEntries(final FDate adjKey, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInCache) {
        Entry<FDate, V> firstInCache = getFirstCachedEntry();
        for (int i = trailingCountFoundInCache - 1; i < trailing.size(); i++) {
            final Entry<FDate, V> prepend = trailing.get(i);
            if (prepend.getKey().isBefore(firstInCache.getKey())) {
                cachedPreviousEntries.add(0, prepend);
                minKeyInDB = prepend.getKey();
                firstInCache = prepend;
            } else {
                break;
            }
        }
        if (maximumSize != null) {
            /*
             * we don't care if the trailing list contains duplicates that were skipped, it still tells that we are
             * interested in this count
             */
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                /*
                 * since we are going further back in time, we have to remove current values. We expect to go further
                 * back and will live with the cost of loading again the current values next time
                 */
                cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
            }
            //so that we don't go accidentally into sameKey algorithm
            cachedPreviousEntriesKey = getLastCachedEntry().getKey();
        }
    }

    private void appendCachedEntries(final FDate adjKey, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInQuery) {
        final Entry<FDate, V> lastInCache = getLastCachedEntry();
        for (int i = trailingCountFoundInQuery - 1; i >= 0; i--) {
            final Entry<FDate, V> append = trailing.get(i);
            if (append.getKey().isAfter(lastInCache.getKey())) {
                cachedPreviousEntries.add(append);
            } else {
                break;
            }
        }
        cachedPreviousEntriesKey = adjKey;
        if (maximumSize != null) {
            /*
             * we don't care if the trailing list contains duplicates that were skipped, it still tells that we are
             * interested in this count
             */
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(0);
            }
        }
    }

    private void replaceCachedEntries(final FDate adjKey, final List<Entry<FDate, V>> trailing) {
        if (trailing.isEmpty() ||
        /*
         * (maybe we went before the first entry) or (maybe we went after the last entry to only fetch one element), so
         * we don't want to throw away a cache that might already be filled
         */
                (trailing.size() == 1 && cachedPreviousEntries.size() > 1)) {
            return;
        }
        maybeIncreaseMaximumSize(trailing.size());
        cachedPreviousEntries.clear();
        cachedPreviousEntries.addAll(trailing);
        //filter duplicates at beginning
        final Entry<FDate, V> firstCachedEntry = getFirstCachedEntry();
        for (int i = 1; i < cachedPreviousEntries.size(); i++) {
            final Entry<FDate, V> nextCachedEntry = cachedPreviousEntries.get(i);
            if (nextCachedEntry.getKey().equals(firstCachedEntry.getKey())) {
                minKeyInDB = firstCachedEntry.getKey();
                cachedPreviousEntries.remove(0);
                i--;
            } else {
                break;
            }
        }
        //filter duplicates at end
        final Entry<FDate, V> lastCachedEntry = getLastCachedEntry();
        for (int i = cachedPreviousEntries.size() - 2; i >= 0; i++) {
            final Entry<FDate, V> prevCachedEntry = cachedPreviousEntries.get(i);
            if (prevCachedEntry.getKey().equals(lastCachedEntry.getKey())) {
                cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
                i--;
            } else {
                break;
            }
        }
        cachedPreviousEntriesKey = adjKey;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);
        if (unitsBack == -1) {
            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            //cached values don't have to be updated
            return trailing;
        } else {
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            Collections.reverse(trailing);
            replaceCachedEntries(adjKey, trailing);
            return trailing;
        }
    }

    private void loadFurtherTrailingValuesViaQuery(final IHistoricalCacheQueryInternalMethods<V> query,
            final List<Entry<FDate, V>> trailing, final int unitsBack) {
        assertUnitsBackNotExchausted(unitsBack);
        final FDate lastTrailingKey = trailing.get(trailing.size() - 1).getKey();
        //we need to load further values from the map
        final int skipFirstValueIncrement = 1;
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, lastTrailingKey,
                unitsBack + skipFirstValueIncrement);
        impl.setIterations(skipFirstValueIncrement);
        int newUnitsBack = unitsBack;
        newUnitsBack = fillFromQuery(trailing, impl, newUnitsBack);
    }

    private int fillFromQuery(final List<Entry<FDate, V>> trailing, final GetPreviousEntryQueryImpl<V> impl,
            final int unitsBack) {
        int newUnitsBack = unitsBack;
        while (newUnitsBack >= 0 && !impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                trailing.add(value);
            } else {
                break;
            }
            newUnitsBack--;
        }
        return newUnitsBack;
    }

    private void assertUnitsBackNotExchausted(final int unitsBack) {
        if (unitsBack < 0) {
            throw new IllegalStateException("unitsBack should not become smaller than -1: " + unitsBack);
        }
    }

    private List<Entry<FDate, V>> defaultGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, adjKey, shiftBackUnits);
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromQuery(trailing, impl, unitsBack);
        Collections.reverse(trailing);
        replaceCachedEntries(adjKey, trailing);
        return trailing;
    }

    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) {
        //prefill what is possible and add suffixes by query as needed
        int cachedIndex = cachedPreviousEntries.size() - 1;
        if (skippingKeysAbove != null) {
            while (cachedIndex >= 0) {
                if (cachedPreviousEntries.get(cachedIndex).getKey().isAfter(skippingKeysAbove)) {
                    cachedIndex--;
                } else {
                    break;
                }
            }
        }
        int newUnitsBack = unitsBack;
        while (newUnitsBack >= 0 && cachedIndex >= 0) {
            trailing.add(cachedPreviousEntries.get(cachedIndex));
            cachedIndex--;
            newUnitsBack--;
        }
        if (newUnitsBack != -1 && cachedIndex == 0) {
            final Entry<FDate, V> firstCachedEntry = getFirstCachedEntry();
            if (firstCachedEntry.getKey().equals(minKeyInDB)) {
                while (newUnitsBack >= 0) {
                    trailing.add(firstCachedEntry);
                    newUnitsBack--;
                }
            }
        }
        return newUnitsBack;
    }

    private Entry<FDate, V> getLastCachedEntry() {
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
    public synchronized void clear() {
        delegate.clear();
        cachedPreviousEntries.clear();
        cachedPreviousEntriesKey = null;
        minKeyInDB = null;
    }

    @Override
    public synchronized void increaseMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
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
    public HistoricalCacheQuery<V> newQuery() {
        return new HistoricalCacheQuery<V>(this);
    }

}
