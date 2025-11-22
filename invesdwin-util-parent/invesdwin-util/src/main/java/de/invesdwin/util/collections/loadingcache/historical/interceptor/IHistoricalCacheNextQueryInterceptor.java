package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheNextQueryInterceptor<V> {

    Optional<? extends IHistoricalEntry<V>> getNextEntry(FDate key, int shiftForwardUnits);

    ICloseableIterable<? extends IHistoricalEntry<V>> getNextEntries(FDate key, int shiftForwardUnits);

}
