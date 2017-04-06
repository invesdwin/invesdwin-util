package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class HistoricalCachePreviousKeysQueryInterceptorSupport
        implements IHistoricalCachePreviousKeysQueryInterceptor {

    @Override
    public Optional<FDate> getPreviousKey(final FDate key, final int shiftBackUnits) {
        return null;
    }

    @Override
    public ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        return null;
    }

}
