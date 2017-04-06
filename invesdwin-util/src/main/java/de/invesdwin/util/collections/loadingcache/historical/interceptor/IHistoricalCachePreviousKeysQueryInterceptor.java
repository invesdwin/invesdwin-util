package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCachePreviousKeysQueryInterceptor {

    Optional<FDate> getPreviousKey(FDate key, int shiftBackUnits);

    ICloseableIterable<FDate> getPreviousKeys(FDate key, int shiftBackUnits);

}
