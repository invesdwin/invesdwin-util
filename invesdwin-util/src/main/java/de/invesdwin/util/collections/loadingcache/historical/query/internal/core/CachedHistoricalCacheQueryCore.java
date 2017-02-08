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
    private final List<Entry<FDate, V>> cachedPreviousEntries = new ArrayList<Entry<FDate, V>>();
    @GuardedBy("this")
    private FDate cachedPreviousEntriesKey = null;
    @GuardedBy("this")
    private List<Entry<FDate, V>> cachedPreviousResult_notFilteringDuplicates = null;
    @GuardedBy("this")
    private List<Entry<FDate, V>> cachedPreviousResult_filteringDuplicates = null;
    @GuardedBy("this")
    private Integer cachedPreviousResult_shiftBackUnits = null;
    @GuardedBy("this")
    private boolean cachedQueryActive;

    public CachedHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new DefaultHistoricalCacheQueryCore<V>(parent);
        this.maximumSize = parent.getInitialMaximumSize();
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

    private void maybeIncreaseMaximumSize(final int requiredSize) {
        if (maximumSize != null && maximumSize < requiredSize) {
            getParent().increaseMaximumSize(requiredSize * REQUIRED_SIZE_MULTIPLICATOR);
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

    private synchronized List<Entry<FDate, V>> getPreviousEntriesList(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits,
            final boolean filterDuplicateKeys) {
        final List<Entry<FDate, V>> result;
        if (cachedQueryActive) {
            //prevent nested/recursive cached queries that might f**k up the cache
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
        } else {
            cachedQueryActive = true;
            try {
                if (!cachedPreviousEntries.isEmpty()) {
                    result = cachedGetPreviousEntries(query, shiftBackUnits, key, filterDuplicateKeys);
                } else {
                    final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                    result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
                    updateCachedPreviousResult(shiftBackUnits, result, filterDuplicateKeys);
                }
            } finally {
                cachedQueryActive = false;
            }
        }
        return result;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final boolean filterDuplicateKeys) {
        if (Objects.equals(key, cachedPreviousEntriesKey) || Objects.equals(key, getLastCachedEntry().getKey())) {
            final List<Entry<FDate, V>> tryCachedPreviousResult = tryCachedPreviousResult_sameKey(query, shiftBackUnits,
                    filterDuplicateKeys);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                final List<Entry<FDate, V>> result = cachedGetPreviousEntries_sameKey(query, shiftBackUnits, key,
                        trailing);
                updateCachedPreviousResult(shiftBackUnits, result, filterDuplicateKeys);
                return result;
            }
        } else if ((key.isAfter(cachedPreviousEntriesKey) || key.isAfter(getLastCachedEntry().getKey()))
                /*
                 * when we go a higher key and only want to load 1 value, we can just go with direct map access since we
                 * won't hit cache anyway because first access will be against map anyway. we make sure to not replace
                 * the cached values in this case in the default query
                 */
                && shiftBackUnits > 1) {
            final List<Entry<FDate, V>> tryCachedPreviousResult = tryCachedPreviousResult_incrementedKey(query,
                    shiftBackUnits, filterDuplicateKeys, key);
            if (tryCachedPreviousResult != null) {
                return tryCachedPreviousResult;
            } else {
                final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
                final List<Entry<FDate, V>> result = cachedGetPreviousEntries_incrementedKey(query, shiftBackUnits, key,
                        trailing);
                updateCachedPreviousResult(shiftBackUnits, result, filterDuplicateKeys);
                return result;
            }
        } else if (key.isBeforeOrEqualTo(cachedPreviousEntriesKey)
                && key.isAfterOrEqualTo(getFirstCachedEntry().getKey())
                && key.isBeforeOrEqualTo(getLastCachedEntry().getKey())) {
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            return cachedGetPreviousEntries_decrementedKey(query, shiftBackUnits, key, trailing);
        } else {
            /*
             * value will not be found in cache (we are before the first cached entry), so we just go with the default
             * query and renew the cache if possible; jumping around wildly in the history is expensive right now
             */
            final List<Entry<FDate, V>> trailing = newEntriesList(query, shiftBackUnits, filterDuplicateKeys);
            final List<Entry<FDate, V>> result = defaultGetPreviousEntries(query, shiftBackUnits, key, trailing);
            updateCachedPreviousResult(shiftBackUnits, result, filterDuplicateKeys);
            return result;
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     */
    private List<Entry<FDate, V>> tryCachedPreviousResult_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits,
            final boolean filterDuplicateKeys, final FDate key) {
        if (cachedPreviousEntries.isEmpty() || cachedPreviousResult_shiftBackUnits == null) {
            return null;
        }
        if (shiftBackUnits > cachedPreviousResult_shiftBackUnits + 1) {
            resetCachedPreviousResult();
            return null;
        }
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        if (cachedPreviousResult_filteringDuplicates != null) {
            final FDate lastCachedResultKey = cachedPreviousResult_filteringDuplicates
                    .get(cachedPreviousResult_filteringDuplicates.size() - 1).getKey();
            assertSameLastKey(lastCachedEntryKey, lastCachedResultKey);
        }
        if (cachedPreviousResult_notFilteringDuplicates != null) {
            final FDate lastCachedResultKey = cachedPreviousResult_notFilteringDuplicates
                    .get(cachedPreviousResult_notFilteringDuplicates.size() - 1).getKey();
            assertSameLastKey(lastCachedEntryKey, lastCachedResultKey);
        }

        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        Entry<FDate, V> latestValue = null;
        while (!impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value == null) {
                resetCachedPreviousResult();
                return null; //abort since we could not find any values
            } else {
                if (value.getKey().equals(lastCachedEntryKey)) {
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
        return tryCachedPreviousResult_sameKey(query, shiftBackUnits, filterDuplicateKeys);
    }

    private void assertSameLastKey(final FDate lastCachedEntryKey, final FDate lastCachedResultKey) {
        if (!lastCachedEntryKey.equals(lastCachedResultKey)) {
            throw new IllegalStateException("lastCachedEntryKey[" + lastCachedEntryKey + "] != lastCachedResultKey["
                    + lastCachedResultKey + "]");
        }
    }

    /**
     * Use sublist if possible to reduce memory footprint of transient array lists to reduce garbage collection overhead
     */
    private List<Entry<FDate, V>> tryCachedPreviousResult_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (filterDuplicateKeys) {
            if (cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
                return null;
            }
            if (cachedPreviousResult_filteringDuplicates == null) {
                if (cachedPreviousResult_notFilteringDuplicates == null) {
                    return null;
                } else {
                    cachedPreviousResult_filteringDuplicates = query
                            .newEntriesList(cachedPreviousResult_shiftBackUnits);
                    cachedPreviousResult_filteringDuplicates.addAll(cachedPreviousResult_notFilteringDuplicates);
                }
            }
            final int toIndex = cachedPreviousResult_filteringDuplicates.size();
            final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
            return Collections.unmodifiableList(cachedPreviousResult_filteringDuplicates.subList(fromIndex, toIndex));
        } else {
            if (cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
                return null;
            }
            if (cachedPreviousResult_notFilteringDuplicates == null) {
                if (cachedPreviousResult_filteringDuplicates == null) {
                    return null;
                } else {
                    cachedPreviousResult_notFilteringDuplicates = new ArrayList<Entry<FDate, V>>(
                            cachedPreviousResult_filteringDuplicates);
                    final int duplicatesRemaining = cachedPreviousResult_shiftBackUnits
                            - cachedPreviousResult_notFilteringDuplicates.size();
                    if (duplicatesRemaining > 0 && !cachedPreviousResult_filteringDuplicates.isEmpty()) {
                        final Entry<FDate, V> toBeDuplicated = cachedPreviousResult_filteringDuplicates.get(0);
                        for (int i = 0; i < duplicatesRemaining; i++) {
                            cachedPreviousResult_notFilteringDuplicates.add(0, toBeDuplicated);
                        }
                    }
                }
            }
            final int toIndex = cachedPreviousResult_notFilteringDuplicates.size();
            final int fromIndex = Math.max(0, toIndex - shiftBackUnits);
            return Collections
                    .unmodifiableList(cachedPreviousResult_notFilteringDuplicates.subList(fromIndex, toIndex));
        }
    }

    private List<Entry<FDate, V>> newEntriesList(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final boolean filterDuplicateKeys) {
        if (filterDuplicateKeys) {
            return query.newEntriesList(shiftBackUnits);
        } else {
            return new ArrayList<Entry<FDate, V>>();
        }
    }

    /**
     * This needs to be called wherever replaceCachedEntries() was called before
     */
    private void updateCachedPreviousResult(final int shiftBackUnits, final List<Entry<FDate, V>> result,
            final boolean filterDuplicateKeys) {
        if (cachedPreviousResult_shiftBackUnits != null && !cachedPreviousEntries.isEmpty()) {
            throw new IllegalStateException("cachedPreviousResult should have been reset by preceeding code!");
        }
        if (filterDuplicateKeys) {
            cachedPreviousResult_filteringDuplicates = result;
            cachedPreviousResult_notFilteringDuplicates = null;
            cachedPreviousResult_shiftBackUnits = shiftBackUnits;
        } else {
            cachedPreviousResult_filteringDuplicates = null;
            cachedPreviousResult_notFilteringDuplicates = result;
            cachedPreviousResult_shiftBackUnits = shiftBackUnits;
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_decrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<Entry<FDate, V>> trailing) {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, key);
        if (unitsBack == -1) {
            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            //cached values don't have to be updated
            return trailing;
        } else {
            final int trailingCountFoundInCache = trailing.size();
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            Collections.reverse(trailing);
            prependCachedEntries(key, trailing, trailingCountFoundInCache);
            return trailing;
        }
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_incrementedKey(
            final IHistoricalCacheQueryInternalMethods<V> query, final int shiftBackUnits, final FDate key,
            final List<Entry<FDate, V>> trailing) {
        int unitsBack = fillFromQueryUntilCacheCanBeUsed(query, shiftBackUnits, key, trailing);

        if (unitsBack == -1) {
            //we can replace the cache since it was too far away anyway
            replaceCachedEntries(key, trailing);
            //we were above the cache and did not find anything useful in the cache, so we filled everything from query
            Collections.reverse(trailing);
            return trailing;
        }

        final int trailingCountFoundInQuery = trailing.size();

        //then fill the rest from the cache as far as possible
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);

        if (unitsBack == -1) {
            //add elments from query since we found newer values and were able to use the cache
            appendCachedEntries(key, trailing, trailingCountFoundInQuery);

            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            return trailing;
        } else {
            //and use the query again if there are missing elements at the end
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            //we can replace the cache since we extended it in both directions
            replaceCachedEntries(key, trailing);
            Collections.reverse(trailing);
            return trailing;
        }
    }

    private int fillFromQueryUntilCacheCanBeUsed(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing) {
        int unitsBack = shiftBackUnits - 1;
        //go through query as long as we found the first entry in the cache
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        final FDate lastCachedEntryKey = getLastCachedEntry().getKey();
        while (unitsBack >= 0 && !impl.iterationFinished()) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (value.getKey().equals(lastCachedEntryKey)) {
                    break; //continue with fillFromCacheAsFarAsPossible
                } else {
                    if (trailing.add(value)) {
                        unitsBack--;
                    } else {
                        unitsBack = -1; //break
                    }
                }
            } else {
                unitsBack = -1; //break
            }
        }
        return unitsBack;
    }

    private void prependCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInCache) {
        for (int i = trailingCountFoundInCache - 1; i < trailing.size(); i++) {
            cachedPreviousEntries.add(trailing.get(i));
        }
        if (maximumSize != null) {
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                /*
                 * since we are going further back in time, we have to remove current values. We expect to go further
                 * back and will live with the cost of loading again the current values next time
                 */
                cachedPreviousEntries.remove(0);
            }
            //so that we don't go accidentally into sameKey algorithm
            cachedPreviousEntriesKey = getLastCachedEntry().getKey();
        }
    }

    private void appendCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing,
            final int trailingCountFoundInQuery) {
        for (int i = trailingCountFoundInQuery - 1; i >= 0; i--) {
            cachedPreviousEntries.add(0, trailing.get(i));
        }
        cachedPreviousEntriesKey = key;
        if (maximumSize != null) {
            maybeIncreaseMaximumSize(trailing.size());
            //ensure we stay in size limit
            while (cachedPreviousEntries.size() > maximumSize) {
                cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
            }
        }
    }

    private void appendCachedEntryAndResult(final FDate key, final int shiftBackUnits,
            final Entry<FDate, V> latestEntry) {
        if (latestEntry != null) {
            cachedPreviousEntries.add(0, latestEntry);

            if (cachedPreviousResult_shiftBackUnits < shiftBackUnits) {
                //we added one more element and there is still demand for more
                cachedPreviousResult_shiftBackUnits++;
            }

            if (cachedPreviousResult_filteringDuplicates != null) {
                //ensure we stay in size limit
                cachedPreviousResult_filteringDuplicates.add(latestEntry);
                if (cachedPreviousResult_filteringDuplicates.size() > cachedPreviousResult_shiftBackUnits) {
                    cachedPreviousResult_filteringDuplicates.remove(0);
                }
            }
            if (cachedPreviousResult_notFilteringDuplicates != null) {
                //ensure we stay in size limit
                cachedPreviousResult_notFilteringDuplicates.add(latestEntry);
                if (cachedPreviousResult_notFilteringDuplicates.size() > cachedPreviousResult_shiftBackUnits) {
                    cachedPreviousResult_notFilteringDuplicates.remove(0);
                }
            }

            if (maximumSize != null) {
                //ensure we stay in size limit
                while (cachedPreviousEntries.size() > maximumSize) {
                    cachedPreviousEntries.remove(cachedPreviousEntries.size() - 1);
                }
            }
        }
        cachedPreviousEntriesKey = key;
    }

    private void replaceCachedEntries(final FDate key, final List<Entry<FDate, V>> trailing) {
        if (trailing.isEmpty() ||
        /*
         * (maybe we went before the first entry) or (maybe we went after the last entry to only fetch one element), so
         * we don't want to throw away a cache that might already be filled
         */
                (trailing.size() == 1 && cachedPreviousEntries.size() > 1)) {
            resetCachedPreviousResult();
            return;
        }
        maybeIncreaseMaximumSize(trailing.size());
        cachedPreviousEntries.clear();
        cachedPreviousEntries.addAll(trailing);
        cachedPreviousEntriesKey = key;
        resetCachedPreviousResult();
    }

    private void resetCachedPreviousResult() {
        cachedPreviousResult_filteringDuplicates = null;
        cachedPreviousResult_notFilteringDuplicates = null;
        cachedPreviousResult_shiftBackUnits = null;
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries_sameKey(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing) {
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack, null);
        if (unitsBack == -1) {
            //maybe cached previous result has smaller shift back
            resetCachedPreviousResult();
            //we could satisfy the query completely with cached values
            Collections.reverse(trailing);
            //cached values don't have to be updated
            return trailing;
        } else {
            loadFurtherTrailingValuesViaQuery(query, trailing, unitsBack);
            replaceCachedEntries(key, trailing);
            Collections.reverse(trailing);
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
                if (!trailing.add(value)) {
                    newUnitsBack = -1; //break
                    break;
                }
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
            final int shiftBackUnits, final FDate key, final List<Entry<FDate, V>> trailing) {
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        int unitsBack = shiftBackUnits - 1;
        unitsBack = fillFromQuery(trailing, impl, unitsBack);
        replaceCachedEntries(key, trailing);
        Collections.reverse(trailing);
        return trailing;
    }

    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack,
            final FDate skippingKeysAbove) {
        //prefill what is possible and add suffixes by query as needed
        int cachedIndex = 0;
        if (skippingKeysAbove != null) {
            while (cachedIndex < cachedPreviousEntries.size()) {
                if (cachedPreviousEntries.get(cachedIndex).getKey().isAfter(skippingKeysAbove)) {
                    cachedIndex++;
                } else {
                    break;
                }
            }
        }

        final int fromIndex = cachedIndex;
        final int toIndex = Math.min(cachedPreviousEntries.size(), fromIndex + unitsBack + 1);
        final int size = toIndex - fromIndex;
        final int newUnitsBack = unitsBack - size;
        final List<Entry<FDate, V>> subList = cachedPreviousEntries.subList(fromIndex, toIndex);
        trailing.addAll(subList);
        return newUnitsBack;
    }

    private Entry<FDate, V> getLastCachedEntry() {
        return cachedPreviousEntries.get(0);
    }

    private Entry<FDate, V> getFirstCachedEntry() {
        return cachedPreviousEntries.get(cachedPreviousEntries.size() - 1);
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
        resetCachedPreviousResult();
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

}
