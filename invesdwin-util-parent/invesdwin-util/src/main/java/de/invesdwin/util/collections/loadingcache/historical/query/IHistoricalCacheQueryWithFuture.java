package de.invesdwin.util.collections.loadingcache.historical.query;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheQueryWithFuture<V> extends IHistoricalCacheQuery<V> {

    @Override
    IHistoricalCacheQueryWithFuture<V> setElementFilter(IHistoricalCacheQueryElementFilter<V> elementFilter);

    /**
     * Warning: throws an exception since withFuture() was already called
     */
    @Deprecated
    @Override
    IHistoricalCacheQueryWithFuture<V> setFutureNullEnabled();

    @Override
    IHistoricalCacheQueryWithFuture<V> setFutureEnabled();

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (above or equal to key), index 1 the next value and so on
     */
    FDate getNextKey(FDate key, int shiftForwardUnits);

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the future.
     * 
     * key is inclusive
     */
    ICloseableIterable<FDate> getNextKeys(FDate key, int shiftForwardUnits);

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (above or equal to key), index 1 the next value and so on
     */
    IHistoricalEntry<V> getNextEntry(FDate key, int shiftForwardUnits);

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit. Null values are skipped.
     * 
     * key is inclusive
     */
    ICloseableIterable<V> getNextValues(FDate key, int shiftForwardUnits);

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the future.
     * 
     * key is inclusive
     */
    ICloseableIterable<IHistoricalEntry<V>> getNextEntries(FDate key, int shiftForwardUnits);

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit.
     * 
     * key is inclusive
     * 
     * index 0 is the current value (above or equal to key), index 1 the next value and so on
     */
    V getNextValue(FDate key, int shiftForwardUnits);

}
