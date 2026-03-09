package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Iterables;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.slf4j.XLoggerDelegateLog;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class RangeHistoricalCacheSizeQueryInterceptor implements IHistoricalCacheSizeQueryInterceptor {

    private static final ILog LOG = new XLoggerDelegateLog(RangeHistoricalCacheSizeQueryInterceptor.class);

    private final IHistoricalCache<?> parent;
    private final IHistoricalCacheRangeQueryInterceptor<?> rangeQueryInterceptor;

    public RangeHistoricalCacheSizeQueryInterceptor(final IHistoricalCache<?> parent,
            final IHistoricalCacheRangeQueryInterceptor<?> rangeQueryInterceptor) {
        this.parent = parent;
        this.rangeQueryInterceptor = rangeQueryInterceptor;
    }

    @Override
    public long size(final FDate from, final FDate to) {
        final ICloseableIterable<? extends IHistoricalEntry<?>> delegate = rangeQueryInterceptor.getEntries(from, to);
        if (delegate == null) {
            return Long.MIN_VALUE;
        }
        return Iterables.sizeLong(LOG, new TextDescription("%s.size(%s, %s, %s)",
                RangeHistoricalCacheSizeQueryInterceptor.class.getSimpleName(), parent, from, to), delegate);
    }

}
