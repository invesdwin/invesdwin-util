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
     */
    FDate getPreviousKey(FDate key, int shiftBackUnits);

    ICloseableIterable<FDate> getPreviousKeys(FDate key, int shiftBackUnits);

    Entry<FDate, V> getPreviousEntry(FDate key, int shiftBackUnits);

    V getPreviousValue(FDate key, int shiftBackUnits);

    ICloseableIterable<Entry<FDate, V>> getPreviousEntries(FDate key, int shiftBackUnits);

    ICloseableIterable<V> getPreviousValues(FDate key, int shiftBackUnits);

    ICloseableIterable<FDate> getKeys(FDate from, FDate to);

    ICloseableIterable<Entry<FDate, V>> getEntries(FDate from, FDate to);

    ICloseableIterable<V> getValues(FDate from, FDate to);

    FDate getPreviousKeyWithSameValueBetween(FDate from, FDate to, V value);

    V getPreviousValueWithSameValueBetween(FDate from, FDate to, V value);

    Entry<FDate, V> getPreviousEntryWithSameValueBetween(FDate from, FDate to, V value);

    FDate getPreviousKeyWithDifferentValueBetween(FDate from, FDate to, V value);

    V getPreviousValueWithDifferentValueBetween(FDate from, FDate to, V value);

    Entry<FDate, V> getPreviousEntryWithDifferentValueBetween(FDate from, FDate to, V value);

    FDate getPreviousKeyWithSameValue(FDate key, int maxShiftBackUnits, V value);

    V getPreviousValueWithSameValue(FDate key, int maxShiftBackUnits, V value);

    Entry<FDate, V> getPreviousEntryWithSameValue(FDate key, int maxShiftBackUnits, V value);

    FDate getPreviousKeyWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

    V getPreviousValueWithDifferentValue(FDate key, int maxShiftBackUnits, V value);

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
