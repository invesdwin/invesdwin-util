package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class HistoricalCacheNextQueryInterceptorSupport<V> implements IHistoricalCacheNextQueryInterceptor<V> {

    @Override
    public Optional<? extends IHistoricalEntry<V>> getNextEntry(final FDate key, final int shiftForwardUnits) {
        return null;
    }

    @Override
    public ICloseableIterable<? extends IHistoricalEntry<V>> getNextEntries(final FDate key,
            final int shiftForwardUnits) {
        return null;
    }

}
