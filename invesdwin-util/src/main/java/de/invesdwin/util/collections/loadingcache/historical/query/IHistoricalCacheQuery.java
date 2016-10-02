package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.Map.Entry;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQuery<V> {

    IHistoricalCacheQuery<V> withElementFilter(IHistoricalCacheQueryElementFilter<V> elementFilter);

    /**
     * Default is true. Filters key and thus values that have already been added to the result list. Thus the result
     * list might contain less values than shiftBackUnits specified.
     */
    IHistoricalCacheQuery<V> withFilterDuplicateKeys(boolean filterDuplicateKeys);

    IHistoricalCacheQuery<V> withRememberNullValue(boolean rememberNullValue);

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

    FDate getPreviousValueKeyBetween(FDate from, FDate to, V value);

}
