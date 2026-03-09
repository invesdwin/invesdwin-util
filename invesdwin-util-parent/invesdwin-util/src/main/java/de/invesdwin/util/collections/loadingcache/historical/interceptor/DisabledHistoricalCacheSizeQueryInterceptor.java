package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;

@Immutable
public final class DisabledHistoricalCacheSizeQueryInterceptor implements IHistoricalCacheSizeQueryInterceptor {

    public static final DisabledHistoricalCacheSizeQueryInterceptor INSTANCE = new DisabledHistoricalCacheSizeQueryInterceptor();

    private DisabledHistoricalCacheSizeQueryInterceptor() {}

    @Override
    public long size(final FDate from, final FDate to) {
        return Long.MIN_VALUE;
    }

}
