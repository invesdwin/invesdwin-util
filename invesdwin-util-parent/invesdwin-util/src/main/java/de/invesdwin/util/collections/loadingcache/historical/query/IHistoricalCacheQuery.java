package de.invesdwin.util.collections.loadingcache.historical.query;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryInternalMethods;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheQuery<V> extends IHistoricalCacheQueryInternalMethods<V> {

    IHistoricalCacheQuery<V> setElementFilter(IHistoricalCacheQueryElementFilter<V> elementFilter);

    IHistoricalCacheQuery<V> setFutureNullEnabled();

    IHistoricalCacheQueryWithFuture<V> setFutureEnabled();

    IHistoricalEntry<V> getEntry(FDate key);

    /**
     * Gets the entry only if it is available in cache, otherwise returns null if it would require computation.
     */
    IHistoricalEntry<V> getEntryIfPresent(FDate key);

    V getValue(FDate key);

    IEvaluateGenericFDate<V> newGetValue();

    IEvaluateGenericFDate<FDate> newGetKey();

    IEvaluateGenericFDate<IHistoricalEntry<V>> newGetEntry();

    ICloseableIterable<IHistoricalEntry<V>> getEntries(Iterable<FDate> keys);

    ICloseableIterable<V> getValues(Iterable<FDate> keys);

    FDate getKey(FDate key);

    /**
     * Jumps the specified shiftBackUnits to the past instead of only one unit. 0 results in current value.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (below or equal to key), index 1 the previous value and so on
     */
    FDate getPreviousKey(FDate key, int shiftBackUnits);

    /**
     * key is inclusive
     */
    ICloseableIterable<FDate> getPreviousKeys(FDate key, int shiftBackUnits);

    /**
     * Jumps the specified shiftBackUnits to the past instead of only one unit. 0 results in current value.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (below or equal to key), index 1 the previous value and so on
     */
    IHistoricalEntry<V> getPreviousEntry(FDate key, int shiftBackUnits);

    /**
     * Jumps the specified shiftBackUnits to the past instead of only one unit. 0 results in current value.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (below or equal to key), index 1 the previous value and so on
     */
    V getPreviousValue(FDate key, int shiftBackUnits);

    /**
     * key is inclusive
     */
    ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(FDate key, int shiftBackUnits);

    /**
     * key is inclusive. Null values are skipped.
     */
    ICloseableIterable<V> getPreviousValues(FDate key, int shiftBackUnits);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<FDate> getKeys(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<IHistoricalEntry<V>> getEntries(FDate from, FDate to);

    /**
     * from and to are inclusive. Null values are skipped.
     */
    ICloseableIterable<V> getValues(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<FDate> getKeysCached(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<IHistoricalEntry<V>> getEntriesCached(FDate from, FDate to);

    /**
     * from and to are inclusive. Null values are skipped.
     */
    ICloseableIterable<V> getValuesCached(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    FDate getPreviousKeyWithSameValueBetween(FDate from, FDate to, V value);

    /**
     * from and to are inclusive
     */
    V getPreviousValueWithSameValueBetween(FDate from, FDate to, V value);

    /**
     * from and to are inclusive
     */
    IHistoricalEntry<V> getPreviousEntryWithSameValueBetween(FDate from, FDate to, V value);

    /**
     * from and to are inclusive
     */
    FDate getPreviousKeyWithDifferentValueBetween(FDate from, FDate to, V value);

    /**
     * from and to are inclusive
     */
    V getPreviousValueWithDifferentValueBetween(FDate from, FDate to, V value);

    /**
     * from and to are inclusive
     */
    IHistoricalEntry<V> getPreviousEntryWithDifferentValueBetween(FDate from, FDate to, V value);

    /**
     * key is inclusive
     */
    FDate getPreviousKeyWithSameValue(FDate key, int maxShiftBackUnits, V value);

    /**
     * key is inclusive
     */
    V getPreviousValueWithSameValue(FDate key, int maxShiftBackUnits, V value);

    /**
     * key is inclusive
     */
    IHistoricalEntry<V> getPreviousEntryWithSameValue(FDate key, int maxShiftBackUnits, V value);

    /**
     * key is inclusive
     */
    FDate getPreviousKeyWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

    /**
     * key is inclusive
     */
    V getPreviousValueWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

    /**
     * key is inclusive
     */
    IHistoricalEntry<V> getPreviousEntryWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

    @SuppressWarnings("rawtypes")
    void copyQuerySettings(IHistoricalCacheQuery copyFrom);

    void resetQuerySettings();

    /**
     * This method bypasses the cache and directly computes the entry.
     */
    IHistoricalEntry<V> computeEntry(FDate key);

    /**
     * This method bypasses the cache and directly computes the key.
     */
    FDate computeKey(FDate key);

    /**
     * This method bypasses the cache and directly computes the value.
     */
    V computeValue(FDate key);

}
