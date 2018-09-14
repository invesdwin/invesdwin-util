package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.Map.Entry;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQuery<V> extends IHistoricalCacheQueryInternalMethods<V> {

    IHistoricalCacheQuery<V> withElementFilter(IHistoricalCacheQueryElementFilter<V> elementFilter);

    /**
     * Default is true. Filters key and thus values that have already been added to the result list. Thus the result
     * list might contain less values than shiftBackUnits specified.
     */
    IHistoricalCacheQuery<V> withFilterDuplicateKeys(boolean filterDuplicateKeys);

    IHistoricalCacheQuery<V> withFutureNull();

    IHistoricalCacheQueryWithFuture<V> withFuture();

    Entry<FDate, V> getEntry(FDate key);

    V getValue(FDate key);

    ICloseableIterable<Entry<FDate, V>> getEntries(Iterable<FDate> keys);

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
     * key is inclusive
     * 
     * index 0 is the current value (below or equal to key), index 1 the previous value and so on
     */
    Entry<FDate, V> getPreviousEntry(FDate key, int shiftBackUnits);

    /**
     * key is inclusive
     * 
     * index 0 is the current value (below or equal to key), index 1 the previous value and so on
     */
    V getPreviousValue(FDate key, int shiftBackUnits);

    /**
     * key is inclusive
     */
    ICloseableIterable<Entry<FDate, V>> getPreviousEntries(FDate key, int shiftBackUnits);

    /**
     * key is inclusive
     */
    ICloseableIterable<V> getPreviousValues(FDate key, int shiftBackUnits);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<FDate> getKeys(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<Entry<FDate, V>> getEntries(FDate from, FDate to);

    /**
     * from and to are inclusive
     */
    ICloseableIterable<V> getValues(FDate from, FDate to);

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
    Entry<FDate, V> getPreviousEntryWithSameValueBetween(FDate from, FDate to, V value);

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
    Entry<FDate, V> getPreviousEntryWithDifferentValueBetween(FDate from, FDate to, V value);

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
    Entry<FDate, V> getPreviousEntryWithSameValue(FDate key, int maxShiftBackUnits, V value);

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
    Entry<FDate, V> getPreviousEntryWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

    @SuppressWarnings("rawtypes")
    void copyQuerySettings(IHistoricalCacheQuery copyFrom);

    void resetQuerySettings();

    /**
     * This method bypasses the cache and directly computes the entry.
     */
    Entry<FDate, V> computeEntry(FDate key);

    /**
     * This method bypasses the cache and directly computes the key.
     */
    FDate computeKey(FDate key);

    /**
     * This method bypasses the cache and directly computes the value.
     */
    V computeValue(FDate key);

}
